package io.github.genkidoudou.common.sign;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.exception.WarningException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 签名工具类
 *
 * @author luyanan
 * @since 2026/11/12
 */
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
    public boolean verify(String sign, SysOauthClientVo sysOauthClientVo,
                          HttpServletRequest request, SignProperties signProperties,
                          NonceStore nonceStore,
                          SignAlgorithm signAlgorithm) {
        String checkSign = sysOauthClientVo.getCheckSign();
        // 不需要签名验证
        if (!checkSign.equals("0")) {
            return true;
        }
        SignTicket signTicket = parser(sign);
        if (StrUtil.hasBlank(signTicket.clientId, signTicket.timestamp, signTicket.nonce, signTicket.hmacHex)) {
            return false;
        }
        String clientId = signTicket.clientId;
        String hmacHex = signTicket.hmacHex;
        String nonce = signTicket.nonce;
        String timestamp = signTicket.timestamp;

        String clientSecret = sysOauthClientVo.getClientSecret();
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request, 1024);
        String contentAsString = wrapper.getContentAsString();


        // 5. 时间戳偏差校验
        long ts;
        try {
            ts = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            throw new WarningException(604);
        }
        // 超时了
        if (Math.abs(System.currentTimeMillis() - ts) > signProperties.getSkewMs()) {
            throw new WarningException(605);
        }

        // 重放

        boolean tryAcquire = nonceStore.tryAcquire(nonce, signProperties.getSkewMs());
        if (!tryAcquire) {
            return false;
        }
        String sign1 = createSign(request, signTicket, signAlgorithm, sysOauthClientVo);
        return signAlgorithm.verify(sign, sysOauthClientVo.getClientSecret(), sign1);

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
        String plain = clientId + '|' + timestamp + '|' + nonce + '|' + hmacHex;
        String b64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(plain.getBytes(StandardCharsets.UTF_8));
        return TICKET_PREFIX + b64;
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
     * 生成签名
     *
     * @param request          请求体
     * @param signTicket       签名票据
     * @param signAlgorithm    加密
     * @param sysOauthClientVo 客户端信息
     * @return
     * @since 2026/11/13
     */

    public String createSign(HttpServletRequest request, SignTicket signTicket, SignAlgorithm signAlgorithm, SysOauthClientVo sysOauthClientVo) {


        //加密
        return signAlgorithm.sign(sysOauthClientVo.getClientSecret(), signBody);

    }


    /**
     * 解包后的四元组
     */
    public record SignTicket(String clientId, String timestamp, String nonce, String hmacHex) {
    }
}
