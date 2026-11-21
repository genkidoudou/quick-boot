package io.github.genkidoudou.common.sign;

import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.spring.CachedBodyHttpServletRequest;
import io.github.genkidoudou.common.utils.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 验签过滤器
 *
 * @author luyanan
 * @since 2026/11/12
 */
@RequiredArgsConstructor
@Slf4j
public class RequestSignFilter extends OncePerRequestFilter {
    public static final String ATTR_CLIENT_ID = "CLIENT_ID";
    private final SignProperties signProperties;

    private final OauthClientSupport oauthClientSupport;

    private final NonceStore nonceStore;

    private final SignAlgorithm signAlgorithm;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 总开关是否开启
        if (!signProperties.isEnable()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 是否匹配忽略url

        if (matchIgnoreUrl(request, signProperties)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析出来oauth
        String sign = request.getHeader("sign");
        if (StrUtil.isBlank(sign)) {
            sign = request.getParameter("sign");
        }
        //  如果签名不存在, 则提示 客户端不能为空
        if (StrUtil.isBlank(sign)) {
            ServletUtils.writeResponse(response, 600);
            return;
        }
        // 解析sign

        SignUtils.SignTicket signTicket = SignUtils.parser(sign);
        if (signTicket == null) {
            // 签名不正确
            ServletUtils.writeResponse(response, 603);
            return;
        }

        SysOauthClientVo oauthClientVo = oauthClientSupport.getClient(signTicket.clientId());
        if (null == oauthClientVo || !"0".equals(oauthClientVo.getStatus())) {
            ServletUtils.writeResponse(response, 600);
            return;
        }

        boolean needHmac = oauthClientVo.hasGrant("sign");
        boolean hmacMode = StrUtil.isAllNotBlank(signTicket.timestamp(), signTicket.nonce(), signTicket.hmacHex());
        // 需要验签但只给了 1 段
        if (needHmac && !hmacMode) {
            ServletUtils.writeResponse(response, 603);
            return;
        }

        try {
            // 不需要验签，或仅标识
            if (!needHmac || !hmacMode) {

                // 绑定客户端上下文，业务可用 OauthClientHolder.get() / hasGrant(...)
                OauthClientHolder.set(oauthClientVo);
                request.setAttribute(ATTR_CLIENT_ID, signTicket.clientId());
                filterChain.doFilter(request, response);
                return;
            }


            // multipart：前端无法用与传输一致的整包 body 加签（FormData / uni.uploadFile），
            // 约定按空 body 验签；且不要把文件流读进缓存，交给 MultipartResolver。
            if (isMultipart(request)) {
                int verify = SignUtils.verify(sign, oauthClientVo, request, signProperties, nonceStore, signAlgorithm, "");
                log.debug("签名验证(multipart):{},{}", signTicket.clientId(), verify);
                if (verify != 0) {
                    ServletUtils.writeResponse(response, verify);
                    return;
                }
                OauthClientHolder.set(oauthClientVo);
                request.setAttribute(ATTR_CLIENT_ID, signTicket.clientId());
                filterChain.doFilter(request, response);
                return;
            }

            // 验证签名（JSON 等可读 body）
            CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);
            int verify = SignUtils.verify(sign, oauthClientVo, request, signProperties, nonceStore, signAlgorithm,
                    wrapped.getBodyAsString());
            log.debug("签名验证:{},{}", signTicket.clientId(), verify);
            if (verify != 0) {
                ServletUtils.writeResponse(response, verify);
                return;
            }

            // 绑定客户端上下文，业务可用 OauthClientHolder.get() / hasGrant(...)
            OauthClientHolder.set(oauthClientVo);
            wrapped.setAttribute(ATTR_CLIENT_ID, signTicket.clientId());
            filterChain.doFilter(wrapped, response);
        } finally {
            OauthClientHolder.remove();
        }
    }

    /**
     * 是否 multipart 上传（含 boundary）。
     */
    private static boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return false;
        }
        return contentType.toLowerCase().startsWith("multipart/");
    }

    /**
     * 是否匹配忽略url
     *
     * @param request        请求
     * @param signProperties 配置文件
     * @return
     * @since 2026/11/12
     */
    private boolean matchIgnoreUrl(HttpServletRequest request, SignProperties signProperties) {
        String path = request.getRequestURI();
        for (String pattern : signProperties.getIgnoreUrls()) {
            if (matcher.match(pattern, path) || matcher.match(pattern, request.getServletPath())) {
                return true;
            }
        }
        return false;
    }
}
