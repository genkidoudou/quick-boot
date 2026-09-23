package io.github.genkidoudou.web.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cn.hutool.core.util.StrUtil;
import io.github.genkidoudou.common.captcha.CaptchaProperties;
import io.github.genkidoudou.common.crypto.PasswordCodec;
import io.github.genkidoudou.common.crypto.PasswordCodecFactories;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.ErrorException;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.core.entity.enums.CommonEnums;
import io.github.genkidoudou.system.api.api.SysUserApi;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * 密码登录 Grant。用户查询须通过 {@code system.api}，禁止依赖 {@code system.internal}。
 */
@Component
@RequiredArgsConstructor
public class PasswordGrantHandler extends AbstractLoginGrantHandler {

    private final CaptchaProperties captchaProperties;

    private final ObjectProvider<ImageCaptchaApplication> imageCaptchaApplicationProvider;


    private final SysUserApi sysUserApi;

    @Override
    public String grantType() {
        return "password";
    }

    @Override
    public LoginUser authenticate(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String uuid = request.getParameter("uuid");
        if (StrUtil.hasBlank(username, password)) {
            throw new ErrorException(7002);
        }
        if (captchaProperties.getEnabled()) {
            ImageCaptchaApplication application = imageCaptchaApplicationProvider.getIfAvailable();
            if (application == null) {
                throw new WarningException(ErrorCodes.Auth.CAPTCHA_SERVICE_UNAVAILABLE);
            }
            if (!(application instanceof SecondaryVerificationApplication secondary)) {
                throw new WarningException(ErrorCodes.Auth.CAPTCHA_SECONDARY_NOT_CONFIGURED);
            }
            if (StrUtil.isBlank(uuid)) {
                throw new WarningException(ErrorCodes.Auth.CAPTCHA_REQUIRED);
            }
            if (!secondary.secondaryVerification(uuid)) {
                throw new WarningException(ErrorCodes.Auth.CAPTCHA_INVALID);
            }
        }
        SysUserVo sysUserVo = sysUserApi.findByUserName(username);
        if (null == sysUserVo) {
            throw new WarningException(7002);
        }
        // 判断状态
        if (!sysUserVo.getStatus().equals(CommonEnums.STATUS_ENABLE.getValue())) {
            throw new WarningException(7003);
        }

        PasswordCodec passwordCodec = PasswordCodecFactories.get(CommonEnums.PASSWORD_CODEC_ENCODED.getValue());
        boolean matches = passwordCodec.matches(password, sysUserVo.getPassword());
        if (!matches) {
            throw new WarningException(7002);
        }
        return buildLoginUser(sysUserVo);
    }
}
