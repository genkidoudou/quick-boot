package io.github.genkidoudou.system.internal.service;

import cn.dev33.satoken.stp.StpUtil;
import io.github.genkidoudou.common.security.service.LoginUserService;
import io.github.genkidoudou.common.security.vo.LoginUser;
import io.github.genkidoudou.core.security.LoginHelper;
import org.springframework.stereotype.Service;

@Service
public class LoginUserServiceImpl implements LoginUserService {
    @Override
    public LoginUser getLoginUser() {
        return LoginHelper.getLogin();
    }
}
