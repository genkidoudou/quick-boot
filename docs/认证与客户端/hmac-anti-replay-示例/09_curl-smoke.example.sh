#!/usr/bin/env bash
# 1 段标识冒烟
set -euo pipefail
CLIENT_ID="${CLIENT_ID:-quick-ui}"
BASE_URL="${BASE_URL:-http://127.0.0.1:9993}"

B64=$(printf '%s' "$CLIENT_ID" | openssl base64 -A | tr '+/' '-_' | tr -d '=')
SIGN="v1.${B64}"
echo "sign=$SIGN"

curl -sS "${BASE_URL}/actuator/health" -H "sign: ${SIGN}" || true
# 业务接口需 qc.sign.enable=true 且路径未在 ignore-urls
