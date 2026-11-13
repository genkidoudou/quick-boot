package docs.example.sign;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

/**
 * 示例：sign 打包 / 解析（1 段标识 / 4 段 HMAC；2、3 段非法）。
 * <pre>
 * sign = v1. + base64url(plain)
 * plain = clientId                              // 仅标识
 * plain = clientId|timestamp|nonce|hmacHex      // 验签
 * </pre>
 * 对照实现时可参考，勿直接当生产类复制进工程而不审阅。
 */
public final class SignCanonical {

    public static final String TICKET_PREFIX = "v1.";
    public static final String HEADER_SIGN = "sign";
    public static final String ATTR_CLIENT_ID = "QC_SIGN_CLIENT_ID";

    private SignCanonical() {
    }

    /**
     * @return 非法（含 2/3 段）时返回 null；1 段时 timestamp/nonce/hmacHex 为 null
     */
    public static SignTicket parse(String signHeader) {
        if (signHeader == null || !signHeader.startsWith(TICKET_PREFIX)) {
            return null;
        }
        try {
            String b64 = signHeader.substring(TICKET_PREFIX.length());
            String plain = new String(Base64.getUrlDecoder().decode(b64), StandardCharsets.UTF_8);
            if (plain.isEmpty()) {
                return null;
            }
            String[] parts = plain.split("\\|", -1);
            if (parts.length == 1) {
                String clientId = parts[0].trim();
                if (clientId.isEmpty()) {
                    return null;
                }
                return new SignTicket(clientId, null, null, null);
            }
            if (parts.length == 4) {
                String clientId = parts[0].trim();
                String timestamp = parts[1].trim();
                String nonce = parts[2].trim();
                String hmacHex = parts[3].trim();
                if (clientId.isEmpty() || timestamp.isEmpty() || nonce.isEmpty() || hmacHex.isEmpty()) {
                    return null;
                }
                return new SignTicket(clientId, timestamp, nonce, hmacHex);
            }
            // 2、3 段：拒绝
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /** 仅标识 */
    public static String packIdentity(String clientId) {
        if (clientId == null || clientId.isBlank() || clientId.contains("|")) {
            throw new IllegalArgumentException("clientId 非法");
        }
        return encode(clientId.trim());
    }

    /** 完整验签四段 */
    public static String packHmac(String clientId, String timestamp, String nonce, String hmacHex) {
        if (clientId == null || clientId.isBlank() || clientId.contains("|")) {
            throw new IllegalArgumentException("clientId 非法");
        }
        return encode(clientId.trim() + '|' + timestamp + '|' + nonce + '|' + hmacHex);
    }

    public static String sha256Hex(byte[] body) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(body == null ? new byte[0] : body));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String canonical(String method, String pathAndQuery,
                                   String timestamp, String nonce, String bodySha256) {
        return method + '\n' + pathAndQuery + '\n' + timestamp + '\n' + nonce + '\n' + bodySha256;
    }

    public static String hmacSha256Hex(String clientSecret, String canonical) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean slowEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r |= a.charAt(i) ^ b.charAt(i);
        }
        return r == 0;
    }

    private static String encode(String plain) {
        String b64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(plain.getBytes(StandardCharsets.UTF_8));
        return TICKET_PREFIX + b64;
    }

    public record SignTicket(String clientId, String timestamp, String nonce, String hmacHex) {
        public boolean hmacMode() {
            return timestamp != null && nonce != null && hmacHex != null;
        }
    }
}
