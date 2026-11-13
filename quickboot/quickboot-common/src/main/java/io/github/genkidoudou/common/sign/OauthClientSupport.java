package io.github.genkidoudou.common.sign;

/**
 * 客户端数据获取
 *
 * @author luyanan
 * @since 2026/11/12
 */
public interface OauthClientSupport {

    /**
     * 根据客户端id查询客户端名称
     *
     * @param clientId 客户端id
     * @return
     * @since 2026/11/12
     */
    SysOauthClientVo getClient(String clientId);

}



