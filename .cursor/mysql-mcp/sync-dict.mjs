import fs from "fs";
import path from "path";
import { fileURLToPath, pathToFileURL } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, "../..");
const sdkDist = path.join(__dirname, "node_modules/@modelcontextprotocol/sdk/dist");
const { Client } = await import(pathToFileURL(path.join(sdkDist, "client/index.js")).href);
const { StdioClientTransport } = await import(pathToFileURL(path.join(sdkDist, "client/stdio.js")).href);

const mcpJson = JSON.parse(fs.readFileSync(path.join(root, ".cursor/mcp.json"), "utf8"));
const env = mcpJson.mcpServers.mysql.env;
const serverJs = path.join(root, ".cursor/mysql-mcp/node_modules/mysql-mcp-server/build/index.js");

async function queryAll(client, sql) {
  const pageSize = 1000;
  let offset = 0;
  const rows = [];
  while (true) {
    const q = `${sql} LIMIT ${pageSize} OFFSET ${offset}`;
    const res = await client.callTool({
      name: "execute_query",
      arguments: { database: env.MYSQL_DATABASE, query: q },
    });
    if (res.isError) {
      throw new Error(res.content?.[0]?.text || "query failed");
    }
    const page = JSON.parse(res.content[0].text);
    rows.push(...page);
    if (page.length < pageSize) break;
    offset += pageSize;
  }
  return rows;
}

const transport = new StdioClientTransport({
  command: process.execPath,
  args: [serverJs],
  env: { ...process.env, ...env },
});
const client = new Client({ name: "quickboot-sync-dict", version: "1.0.0" }, { capabilities: {} });
await client.connect(transport);

const types = await queryAll(client, "SELECT * FROM sys_dict_type");
const data = await queryAll(client, "SELECT * FROM sys_dict_data");

const outDir = path.join(root, "sdd/dict");
fs.mkdirSync(outDir, { recursive: true });
fs.writeFileSync(path.join(outDir, "sys_dict_type.json"), JSON.stringify(types, null, 2) + "\n", "utf8");
fs.writeFileSync(path.join(outDir, "sys_dict_data.json"), JSON.stringify(data, null, 2) + "\n", "utf8");

const typeHas = types[0] && Object.prototype.hasOwnProperty.call(types[0], "dict_type");
const dataHas = data[0] && Object.prototype.hasOwnProperty.call(data[0], "dict_type");
let byTypeWritten = false;
const columns = {
  type: types[0] ? Object.keys(types[0]) : [],
  data: data[0] ? Object.keys(data[0]) : [],
};

if (typeHas && dataHas) {
  const byType = {};
  for (const t of types) {
    const key = t.dict_type;
    byType[key] = {
      dictName: t.dict_name ?? null,
      status: t.status ?? null,
      items: [],
    };
  }
  for (const row of data) {
    const key = row.dict_type;
    if (!byType[key]) {
      byType[key] = { dictName: null, status: null, items: [] };
    }
    byType[key].items.push(row);
  }
  for (const key of Object.keys(byType)) {
    const items = byType[key].items;
    if (items.length && Object.prototype.hasOwnProperty.call(items[0], "dict_sort")) {
      items.sort((a, b) => Number(a.dict_sort ?? 0) - Number(b.dict_sort ?? 0));
    }
  }
  fs.writeFileSync(path.join(outDir, "by-type.json"), JSON.stringify(byType, null, 2) + "\n", "utf8");
  byTypeWritten = true;
}

await client.close();
console.log(JSON.stringify({
  typeCount: types.length,
  dataCount: data.length,
  byTypeWritten,
  columns,
}, null, 2));
