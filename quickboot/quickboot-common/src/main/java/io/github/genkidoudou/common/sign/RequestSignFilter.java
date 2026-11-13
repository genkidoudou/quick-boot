package io.github.genkidoudou.common.sign;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.utils.ServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

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

    private final SignProperties signProperties;

    private final OauthClientSupport oauthClientSupport;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 总开关是否开启
        if (!signProperties.isEnable()) {
            return;
        }

        // 2. 是否匹配忽略url

        if (matchIgnoreUrl(request, signProperties)) {
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
        if (null == signTicket) {
            // 签名不正确
            ServletUtils.writeResponse(response, 603);
            return;
        }

        SysOauthClientVo oauthClientVo = oauthClientSupport.getClient(signTicket.clientId());
        if (null == oauthClientVo) {
            ServletUtils.writeResponse(response, 600);
            return;
        }

        String checkSign = oauthClientVo.getCheckSign();
        if (!checkSign.equals("0")) {
            return;
        }

        // 验证签名
        ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(request, 1024);
        String contentAsString = contentCachingRequestWrapper.getContentAsString();

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
