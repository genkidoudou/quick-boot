package io.github.genkidoudou.common.datascope;

import io.github.genkidoudou.common.datascope.annotation.DataPermission;
import io.github.genkidoudou.common.security.vo.LoginUser;

public interface DataScopeSupport {


    DataScopeContext.DataScope getScope(LoginUser loginUser, DataPermission dataPermission);
}
