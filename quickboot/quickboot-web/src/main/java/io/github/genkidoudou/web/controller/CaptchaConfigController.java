package io.github.genkidoudou.web.controller;

import io.github.genkidoudou.common.captcha.CaptchaProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 登录页验证码开关（不受 {@code qc.captcha.enabled} 条件装配影响，关闭时也返回 false）。
 */
@Tag(name = "验证码配置")
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaConfigController {

    private final CaptchaProperties captchaProperties;

    /**
     * @return {@code captchaEnabled} / {@code type}
     */
    @Operation(summary = "获取验证码配置")
    @GetMapping("/config")
    public Map<String, Object> config() {
        Map<String, Object> data = new LinkedHashMap<>(4);
        data.put("captchaEnabled", true);
        data.put("type", captchaProperties.getType());
        return data;
    }
}
