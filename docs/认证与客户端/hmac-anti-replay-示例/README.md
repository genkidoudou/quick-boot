# sign 头示例（标识 / HMAC 兼容）

设计：[2026-11-12-request-hmac-anti-replay-design.md](../../superpowers/specs/2026-11-12-request-hmac-anti-replay-design.md)

**仅文档示例**，请自行对照改工程代码，本目录文件不会自动进 classpath。

```text
sign = v1. + base64url(plain)

plain = clientId                              # 1 段：只标识
plain = clientId|timestamp|nonce|hmacHex      # 4 段：验签
# 2、3 段：非法
```

| 文件 | 说明 |
|------|------|
| `01_SignCanonical.example.java` | pack/parse + HMAC |
| `02_SignProperties.example.java` / `02_application-sign.example.yml` | 配置 |
| `03_NonceStore.example.java` | Redis 防重放 |
| `04_RequestSignFilter.example.java` | Filter 正确骨架（必 doFilter；禁用 ContentCaching 先验签） |
| `05_sign.example.ts` | 前端 packIdentity / packHmac |
| `06_axios-interceptor.example.ts` | quick-ui 示意 |
| `07_uni-http-sign.example.ts` | quick-h5 示意 |
| `08_env.example` | env |
| `09_curl-smoke.example.sh` | 1 段 curl |
