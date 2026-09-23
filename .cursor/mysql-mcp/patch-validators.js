const fs = require("fs");
const path = require("path");

const target = path.join(
  __dirname,
  "node_modules",
  "mysql-mcp-server",
  "build",
  "validators.js"
);

if (!fs.existsSync(target)) {
  console.error("patch-validators: missing", target);
  process.exit(1);
}

let src = fs.readFileSync(target, "utf8");
if (src.includes("Allow SHOW CREATE")) {
  console.log("patch-validators: already patched");
  process.exit(0);
}

const patchedBlock = [
  "const containsDisallowed = DISALLOWED_COMMANDS.some(cmd => {",
  "        // Allow SHOW CREATE TABLE / VIEW / ... — CREATE here is not DDL.",
  "        if (cmd === 'CREATE' && /^SHOW\\s+CREATE\\b/.test(normalizedQuery)) {",
  "            return false;",
  "        }",
  "        const regex = new RegExp(`(^|\\\\s)${cmd}(\\\\s|$)`);",
  "        return regex.test(normalizedQuery);",
  "    });",
].join("\n");

const next = src.replace(
  /const containsDisallowed = DISALLOWED_COMMANDS\.some\(cmd => \{[\s\S]*?\}\);/,
  patchedBlock
);

if (next === src) {
  console.error("patch-validators: pattern not found");
  process.exit(1);
}

fs.writeFileSync(target, next);
console.log("patch-validators: patched", target);
