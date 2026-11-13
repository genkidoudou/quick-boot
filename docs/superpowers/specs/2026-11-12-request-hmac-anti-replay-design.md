# 请求防篡改：sign 头（标识 / HMAC 兼容）

日期：2026-11-12  
状态：已确认  
关联示例：`docs/认证与客户端/hmac-anti-replay-示例/`  

> 本设计与示例仅供对照；**工程内 `quickboot-common/sign` 由开发者自行按示例改造**。

## 1. 目标

同一请求头 **`sign`**，同一打包格式，按明文**段数**区分：

| 段数 | 明文 | 行为 |
|------|------|------|
| **1** | `clientId` | 仅客户端标识，不验签、不防重放 |
| **4** | `clientId\|timestamp\|nonce\|hmacHex` | HMAC-SHA256 + 时间窗 + Redis nonce |
| 2 / 3 | — | **拒绝** |

## 2. 协议

```text
sign = v1. + base64url(UTF-8(plain))
```

- 标识：`plain = quick-ui`
- 验签：`plain = quick-ui|{ms}|{uuid}|{hmacHex}`
- canonical（仅 4 段）：

```text
{METHOD}\n{pathAndQuery}\n{timestamp}\n{nonce}\n{bodySha256Hex}
```

`Authorization: Bearer …` 与 sign 独立。

## 3. 配置建议 `qc.sign`

| 项 | 建议默认 | 说明 |
|----|----------|------|
| `enable` | `false` | 前端加签后再开 |
| `require-hmac` | `false` | `true` 时禁止 1 段 |
| `header-name` | `sign` | |
| `skew-ms` | 300000 | |
| `nonce-ttl-seconds` | 600 | |
| `ignore-urls` | 探活等 | |

## 4. 错误码建议

| 码 | 含义 |
|----|------|
| 600 | 客户端不存在 / 无密钥 |
| 603 | 缺少或非法 sign |
| 604 | 签名错误 |
| 605 | 请求过期 |
| 10004 | 重复 nonce |

## 5. 安全说明

1 段可伪造；4 段前端 secret 可被拆。HTTPS + Bearer 仍是主防线。
