package docs.example.sign;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 示例 Filter 正确骨架（对照工程 RequestSignFilter 时用）。
 *
 * <h3>必须遵守</h3>
 * <ul>
 *   <li>任何「放行」路径都要 {@code filterChain.doFilter(...)}，禁止裸 {@code return}</li>
 *   <li>不要用 {@code ContentCachingRequestWrapper} 做「Filter 先验签」——
 *       {@code getContentAsString()} 在下游未读流前通常是空的，且读完后下游再读会空 body。
 *       应使用下方 {@link CachedBodyRequest} 这类可重复读 Wrapper，并把 wrapper 传给 doFilter</li>
 *   <li>1 段 ticket：只标识，设 ATTR 后 doFilter</li>
 *   <li>4 段 ticket：时间窗 + nonce + HMAC，成功则 doFilter(wrapper)</li>
 * </ul>
 *
 * <h3>错误码建议</h3>
 * 600 客户端无效；603 缺/非法 sign；604 签名错；605 过期；10004 重放
 */
public class RequestSignFilterExample extends OncePerRequestFilter {

    public static final int CODE_CLIENT_MISSING = 600;
    public static final int CODE_SIGN_INVALID = 603;
    public static final int CODE_SIGN_MISMATCH = 604;
    public static final int CODE_SIGN_EXPIRED = 605;
    public static final int CODE_REPLAY = 10004;

    /**
     * 示例：客户端配置。工程里换成 OauthClientSupport / SysOauthClientVo。
     *
     * @param checkSign true=该客户端要求 4 段 HMAC；false=允许仅 1 段标识
     */
    public record ClientInfo(String clientId, String clientSecret, boolean checkSign, boolean enabled) {
    }

    @FunctionalInterface
    public interface ClientLookup {
        /** 不存在或停用返回 null */
        ClientInfo find(String clientId);
    }

    private final SignPropertiesExample props;
    private final ClientLookup clientLookup;
    private final NonceStore nonceStore; // 可为 null：跳过防重放（仅示例）
    private final AntPathMatcher matcher = new AntPathMatcher();

    public RequestSignFilterExample(SignPropertiesExample props, ClientLookup clientLookup, NonceStore nonceStore) {
        this.props = props;
        this.clientLookup = clientLookup;
        this.nonceStore = nonceStore;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // ---------- 1. 总开关：关闭则整链放行 ----------
        if (!props.isEnable()) {
            chain.doFilter(request, response);
            return;
        }

        // ---------- 2. 忽略 URL：放行 ----------
        if (matchIgnoreUrl(request)) {
            chain.doFilter(request, response);
            return;
        }

        // ---------- 3. 取 sign 头（或参数）----------
        String headerName = blankToDefault(props.getHeaderName(), SignCanonical.HEADER_SIGN);
        String raw = firstNonBlank(request.getHeader(headerName), request.getParameter(headerName));
        if (raw == null || raw.isBlank()) {
            reject(response, CODE_SIGN_INVALID); // 不要用 600「客户端不存在」
            return;
        }

        SignCanonical.SignTicket ticket = SignCanonical.parse(raw);
        if (ticket == null) {
            // 含 2/3 段非法、前缀错误、Base64 坏等
            reject(response, CODE_SIGN_INVALID);
            return;
        }

        ClientInfo client = clientLookup.find(ticket.clientId());
        if (client == null || !client.enabled()) {
            reject(response, CODE_CLIENT_MISSING);
            return;
        }

        // 全局强制 HMAC，或客户端配置要求验签，但票据却是 1 段 → 拒绝
        boolean needHmac = props.isRequireHmac() || client.checkSign();
        if (needHmac && !ticket.hmacMode()) {
            reject(response, CODE_SIGN_INVALID);
            return;
        }

        // ---------- 4. 仅标识（1 段，或不要求 HMAC）----------
        if (!ticket.hmacMode()) {
            request.setAttribute(SignCanonical.ATTR_CLIENT_ID, ticket.clientId());
            chain.doFilter(request, response); // 必须放行
            return;
        }

        // ---------- 5. 完整验签（4 段）：可重复读 Wrapper ----------
        CachedBodyRequest wrapped = new CachedBodyRequest(request);
        if (!verifyHmac(wrapped, response, ticket, client)) {
            return; // 已 writeResponse，不再 doFilter
        }
        wrapped.setAttribute(SignCanonical.ATTR_CLIENT_ID, ticket.clientId());
        chain.doFilter(wrapped, response); // 下游读的是缓存流，body 仍在
    }

    private boolean verifyHmac(CachedBodyRequest req, HttpServletResponse response,
                               SignCanonical.SignTicket ticket, ClientInfo client) throws IOException {
        long ts;
        try {
            ts = Long.parseLong(ticket.timestamp());
        } catch (NumberFormatException e) {
            reject(response, CODE_SIGN_INVALID);
            return false;
        }
        if (Math.abs(System.currentTimeMillis() - ts) > props.getSkewMs()) {
            reject(response, CODE_SIGN_EXPIRED);
            return false;
        }

        if (client.clientSecret() == null || client.clientSecret().isBlank()) {
            reject(response, CODE_CLIENT_MISSING);
            return false;
        }

        if (nonceStore != null && !nonceStore.tryAcquire(ticket.clientId(), ticket.nonce())) {
            reject(response, CODE_REPLAY);
            return false;
        }

        String method = req.getMethod().toUpperCase();
        String pathAndQuery = buildPathAndQuery(req);
        // 与前端约定一致：对「原始 body 字节」做 SHA-256；无 body 用空数组
        String bodyHash = SignCanonical.sha256Hex(req.getCachedBody());
        String canonical = SignCanonical.canonical(
                method, pathAndQuery, ticket.timestamp(), ticket.nonce(), bodyHash);
        String expect = SignCanonical.hmacSha256Hex(client.clientSecret(), canonical);
        if (!SignCanonical.slowEquals(expect, ticket.hmacHex().toLowerCase())) {
            reject(response, CODE_SIGN_MISMATCH);
            return false;
        }
        return true;
    }

    private String buildPathAndQuery(HttpServletRequest req) {
        // 必须与前端签名时的 path 一致（是否含 context-path、是否已去掉 /dev-api）
        String path = req.getRequestURI();
        String qs = req.getQueryString();
        return qs == null || qs.isEmpty() ? path : path + "?" + qs;
    }

    private boolean matchIgnoreUrl(HttpServletRequest request) {
        for (String pattern : props.getIgnoreUrls()) {
            if (matcher.match(pattern, request.getRequestURI())
                    || matcher.match(pattern, request.getServletPath())) {
                return true;
            }
        }
        return false;
    }

    private void reject(HttpServletResponse response, int code) throws IOException {
        // 工程里请换成 ServletUtils.writeResponse(response, code) + i18n
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"code\":" + code + ",\"msg\":\"sign rejected\"}");
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a.trim();
        }
        if (b != null && !b.isBlank()) {
            return b.trim();
        }
        return null;
    }

    private static String blankToDefault(String v, String dft) {
        return v == null || v.isBlank() ? dft : v;
    }

    /**
     * 可重复读 body 的 Wrapper（验签场景请用这个，不要用 ContentCachingRequestWrapper）。
     */
    static final class CachedBodyRequest extends HttpServletRequestWrapper {
        private final byte[] cached;

        CachedBodyRequest(HttpServletRequest request) throws IOException {
            super(request);
            cached = request.getInputStream().readAllBytes();
        }

        byte[] getCachedBody() {
            return cached;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream in = new ByteArrayInputStream(cached);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return in.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                }

                @Override
                public int read() {
                    return in.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}
