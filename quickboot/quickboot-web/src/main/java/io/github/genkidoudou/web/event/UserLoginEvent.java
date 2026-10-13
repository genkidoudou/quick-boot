package io.github.genkidoudou.web.event;

import org.springframework.context.ApplicationEvent;

/**
 * 用户登录结果事件（成功 / 失败均发布）。
 */
public class UserLoginEvent extends ApplicationEvent {

    public UserLoginEvent(UserLoginEventVo source) {
        super(source);
    }

    public UserLoginEventVo getLoginEventVo() {
        return (UserLoginEventVo) getSource();
    }
}
