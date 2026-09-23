package io.github.genkidoudou.core.constants;

/**
 * 常用业务字段正则常量，供 {@code @Pattern(regexp = ...)} 等校验使用。
 *
 * @author luyanan
 * @since 2026/9/23
 */
public final class PatternConstants {

    private PatternConstants() {
    }

    /**
     * 密码：至少 8 位，且必须同时包含大写字母、小写字母、数字与特殊字符。
     * <p>特殊字符范围：{@code !@#$%^&*()_+-=[]{}|;:',.&lt;&gt;?/~`}
     */
    public static final String PASS_WORD =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:',.<>?/~`]).{8,}$";

    /**
     * 中国大陆手机号：1 开头，第二位 3–9，共 11 位数字。
     */
    public static final String PHONE = "^1[3-9]\\d{9}$";

    /**
     * 用户名：长度大于 6（至少 7 位），仅允许英文字母与数字，不允许汉字、空格及特殊符号。
     */
    public static final String USER_NAME = "^[a-zA-Z0-9]{7,}$";
}
