import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import mysql from "mysql2/promise.js";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const root = path.resolve(__dirname, "../..");
const mcp = JSON.parse(fs.readFileSync(path.join(root, ".cursor/mcp.json"), "utf8"));
const env = mcp.mcpServers.mysql.env;
const db = env.MYSQL_DATABASE;

const conn = await mysql.createConnection({
  host: env.MYSQL_HOST,
  port: Number(env.MYSQL_PORT),
  user: env.MYSQL_USER,
  password: env.MYSQL_PASSWORD,
  database: db,
  supportBigNumbers: true,
  bigNumberStrings: true,
  dateStrings: true,
});

const ddlDir = path.join(root, "sdd/ddl");
const dictDir = path.join(root, "sdd/dict");
fs.mkdirSync(ddlDir, { recursive: true });
fs.mkdirSync(dictDir, { recursive: true });

const [tables] = await conn.query(
  `SELECT TABLE_NAME AS name FROM information_schema.TABLES
   WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = 'BASE TABLE'
   ORDER BY TABLE_NAME`,
  [db],
);
const tableNames = tables.map((t) => t.name);
const existing = new Set(
  fs.readdirSync(ddlDir).filter((f) => f.endsWith(".sql")).map((f) => f.slice(0, -4)),
);

for (const name of tableNames) {
  const [rows] = await conn.query(`SHOW CREATE TABLE \`${name}\``);
  const ddl = rows[0]["Create Table"];
  fs.writeFileSync(path.join(ddlDir, `${name}.sql`), ddl.trimEnd() + ";\n", "utf8");
  existing.delete(name);
}
const removed = [...existing];
for (const stale of removed) {
  fs.unlinkSync(path.join(ddlDir, `${stale}.sql`));
}

async function queryAll(sql) {
  const pageSize = 1000;
  let offset = 0;
  const all = [];
  while (true) {
    const [rows] = await conn.query(`${sql} LIMIT ${pageSize} OFFSET ${offset}`);
    all.push(...rows);
    if (rows.length < pageSize) break;
    offset += pageSize;
  }
  return all;
}

const types = await queryAll("SELECT * FROM sys_dict_type ORDER BY id");
const data = await queryAll("SELECT * FROM sys_dict_data ORDER BY id");
fs.writeFileSync(path.join(dictDir, "sys_dict_type.json"), JSON.stringify(types, null, 2) + "\n", "utf8");
fs.writeFileSync(path.join(dictDir, "sys_dict_data.json"), JSON.stringify(data, null, 2) + "\n", "utf8");

const byType = {};
for (const t of types) {
  byType[t.dict_type] = {
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
  byType[key].items.sort((a, b) => Number(a.dict_sort ?? 0) - Number(b.dict_sort ?? 0));
}
fs.writeFileSync(path.join(dictDir, "by-type.json"), JSON.stringify(byType, null, 2) + "\n", "utf8");

await conn.end();
console.log(JSON.stringify({
  tableCount: tableNames.length,
  removedDdl: removed,
  typeCount: types.length,
  dataCount: data.length,
  dictTypeKeys: Object.keys(byType).length,
}, null, 2));
