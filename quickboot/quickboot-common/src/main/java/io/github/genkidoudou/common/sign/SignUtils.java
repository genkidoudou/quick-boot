package io.github.genkidoudou.common.sign;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 *
 * @author luyanan
 * @since 2026/11/12
 */
@Slf4j
@UtilityClass
public class SignUtils {
    public static final String TICKET_PREFIX = "v1.";

    /**
     * 解析
     *
     * @param signHeader 签名头
     * @return
     * @since 2026/11/12
     */
    public SignTicket parser(String signHeader) {
        if (signHeader == null || !signHeader.startsWith(TICKET_PREFIX)) {
            return null;
        }
        String b64 = signHeader.substring(TICKET_PREFIX.length());
        String plain = new String(Base64.getUrlDecoder().decode(b64), StandardCharsets.UTF_8);
        if (plain.isEmpty()) {
            return null;
        }
        String[] parts = plain.split("\\|", -1);
        if (parts.length == 1) {
            String clientId = parts[0].trim();
            if (StrUtil.isBlank(clientId)) {
                return null;
            }
            return new SignTicket(clientId, null, null, null);
        }
        if (parts.length == 4) {
            String clientId = parts[0].trim();
            String timestamp = parts[1].trim();
            String nonce = parts[2].trim();
            String hmacHex = parts[3].trim();
            if (StrUtil.hasBlank(clientId, timestamp, nonce, hmacHex)) {
                return null;
            }
            return new SignTicket(clientId, timestamp, nonce, hmacHex);
        }
        return null;
    }


    /**
     * 验证签名
     *
     * @param sign
     * @param sysOauthClientVo
     * @param request          请求体
     * @return
     * @since 2026/11/13
     */
    public int verify(String sign, SysOauthClientVo sysOauthClientVo,
                      HttpServletRequest request, SignProperties signProperties,
                      NonceStore nonceStore,
                      SignAlgorithm signAlgorithm,
                      String bodyUtf8) {
        // grant_types 含 sign 才验 HMAC；否则视为仅标识
        if (!sysOauthClientVo.hasGrant("sign")) {
            return 0;
        }
        SignTicket ticket = parser(sign);
        if (ticket == null
                || StrUtil.hasBlank(ticket.clientId(), ticket.timestamp(), ticket.nonce(), ticket.hmacHex())) {
            return 603;
        }
        long ts;
        try {
            ts = Long.parseLong(ticket.timestamp());
        } catch (NumberFormatException e) {
            return 603;
        }
        if (Math.abs(System.currentTimeMillis() - ts) > signProperties.getSkewMs()) {
            return 605;
        }
        if (StrUtil.isBlank(sysOauthClientVo.getClientSecret())) {
            return 600;
        }
        // 注意：第二个参数是秒，不要传 skewMs
        if (!nonceStore.tryAcquire(ticket.nonce(), signProperties.getNonceTtlSeconds())) {
            return 10004;
        }
        String queryParams = queryParams(request);
        log.debug("查询参数:{}", queryParams);

        // 无 query 时不要拼 "?"，否则会变成 /api/foo? 与前端 /api/foo 不一致 → 604
        String pathAndQuery = request.getRequestURI();
        if (StrUtil.isNotBlank(queryParams)) {
            pathAndQuery = pathAndQuery + "?" + queryParams;
        }

        // 把「原文」交给 verify，不要先 createHmacHex 再二次 HMAC
        String payload = SignUtils.buildSignBody(
                request.getMethod(), pathAndQuery,
                ticket.timestamp(), ticket.nonce(), bodyUtf8, ticket.clientId());
        return signAlgorithm.verify(ticket.hmacHex(), sysOauthClientVo.getClientSecret(), payload) ? 0 : 604;

    }

    private String queryParams(HttpServletRequest request) {
        String raw = request.getQueryString();
        if (raw == null || raw.isBlank()) {
            return "";
        }

        // 先按 & 切开，再按 key 排序；值保留解码前或统一重新 encode，前后端要同一策略。
        // 这里策略：对每个 pair 的 raw key/value 先 URLDecoder 再按统一规则 encode，消除 %XX 写法差异。
        TreeMap<String, List<String>> sorted = new TreeMap<>();
        for (String pair : raw.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            int i = pair.indexOf('=');
            String k;
            String v;
            if (i < 0) {
                k = decode(pair);
                v = "";
            } else {
                k = decode(pair.substring(0, i));
                v = decode(pair.substring(i + 1));
            }
            sorted.computeIfAbsent(k, ignore -> new ArrayList<>()).add(v);
        }

        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : sorted.entrySet()) {
            for (String v : e.getValue()) {
                pairs.add(enc(e.getKey()) + "=" + enc(v));
            }
        }
        return String.join("&", pairs);
    }

    /**
     * 与前端 encodeURIComponent 对齐时，注意 Java URLEncoder 空格→+，见 TS 侧 formEncode。
     */
    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String decode(String s) {
        return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    /**
     * 生成 HMAC hex（不是完整 sign 头）。
     *
     * @param bodyUtf8 原始请求体；无 body 传 {@code ""}
     */
    public static String createHmacHex(HttpServletRequest request,
                                       SignUtils.SignTicket ticket,
                                       SignAlgorithm signAlgorithm,
                                       SysOauthClientVo client,
                                       String bodyUtf8) {
        String path = request.getRequestURI();
        String payload = SignUtils.buildSignBody(
                request.getMethod(),
                path,
                ticket.timestamp(),
                ticket.nonce(),
                bodyUtf8 == null ? "" : bodyUtf8,
                ticket.clientId());
        return signAlgorithm.sign(client.getClientSecret(), payload);
    }

    /**
     * 打成不透明 sign 头值。
     *
     * @param clientId  勿包含 {@code |}
     * @param timestamp 毫秒字符串
     * @param nonce     随机串
     * @param hmacHex   小写 hex
     */
    public String packSignHeader(String clientId, String timestamp, String nonce, String hmacHex) {
        if (StrUtil.hasBlank(timestamp, nonce, hmacHex)) {
            String plain = clientId;
            String b64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(plain.getBytes(StandardCharsets.UTF_8));
            return TICKET_PREFIX + b64;
        } else {
            String plain = clientId + '|' + timestamp + '|' + nonce + '|' + hmacHex;
            String b64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(plain.getBytes(StandardCharsets.UTF_8));
            return TICKET_PREFIX + b64;
        }

    }

    /**
     * 按约定拼接签名原文。
     *
     * @param method        HTTP 方法（将规范为大写）
     * @param path          不含 query 的路径
     * @param timestamp     毫秒时间戳字符串
     * @param nonce         一次性随机串
     * @param canonicalBody UTF-8 原始 body；无 body 传空串
     * @return 签名原文
     */
    public static String buildSignBody(String method, String path, String timestamp, String nonce, String canonicalBody, String clientId) {
        String m = method == null ? "" : method.toUpperCase();
        String p = path == null ? "" : path;
        String ts = timestamp == null ? "" : timestamp;
        String n = nonce == null ? "" : nonce;
        String body = canonicalBody == null ? "" : canonicalBody;
        // 五段固定用 \n 连接，末段允许为空
        return clientId + "\n" + m + "\n" + p + "\n" + ts + "\n" + n + "\n" + body;
    }


    /**
     * 解包后的四元组
     */
    public record SignTicket(String clientId, String timestamp, String nonce, String hmacHex) {
    }

    public static void main(String[] args) {
        String s = SignUtils.packSignHeader("web", null, null, null);
        System.out.println(s);
    }
}
