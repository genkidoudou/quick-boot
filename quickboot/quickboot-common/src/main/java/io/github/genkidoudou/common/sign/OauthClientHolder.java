package io.github.genkidoudou.common.sign;

import cn.hutool.core.lang.Opt;

/**
 * 当前请求已解析的 OAuth 客户端（ThreadLocal）。
 * <p>
 * 由 {@link RequestSignFilter} 在验签/标识通过后 {@link #set}，请求结束 {@link #remove}。
 * 业务侧（登录、按 grant_types 判断 captcha/sign 等）调用 {@link #get()}。
 * <p>
 * 注意：必须在 finally 中 remove，否则线程复用会串客户端。
 */
public final class OauthClientHolder {

    private static final ThreadLocal<SysOauthClientVo> HOLDER = new ThreadLocal<>();

    private OauthClientHolder() {
    }

    /**
     * 绑定当前请求的客户端。
     *
     * @param client 已查库且状态正常的客户端；可为 null（等价 clear）
     */
    public static void set(SysOauthClientVo client) {
        if (client == null) {
            HOLDER.remove();
            return;
        }
        HOLDER.set(client);
    }

    /**
     * @return 当前请求客户端；未经过 Filter 或已 remove 时为 null
     */
    public static SysOauthClientVo get() {
        return HOLDER.get();
    }

    public static Opt<SysOauthClientVo> getOpt() {
        return Opt.ofNullable(HOLDER.get());
    }

    /**
     * 是否包含指定能力（委托 {@link SysOauthClientVo#hasGrant}）。
     *
     * @param code password / captcha / sign 等
     * @return 无客户端时 false
     */
    public static boolean hasGrant(String code) {
        SysOauthClientVo client = get();
        return client != null && client.hasGrant(code);
    }

    /**
     * 清理，防止线程池泄漏。
     */
    public static void remove() {
        HOLDER.remove();
    }
}
