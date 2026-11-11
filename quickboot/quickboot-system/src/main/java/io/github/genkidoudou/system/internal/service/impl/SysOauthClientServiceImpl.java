package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysOauthClient;
import io.github.genkidoudou.system.internal.mapper.SysOauthClientMapper;
import io.github.genkidoudou.system.internal.service.ISysOauthClientService;
import io.github.genkidoudou.system.internal.vo.SysOauthClientVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OAuth 客户端服务实现。
 */
@Service
public class SysOauthClientServiceImpl extends BaseVoServiceImpl<SysOauthClientMapper, SysOauthClient, SysOauthClientVo>
        implements ISysOauthClientService {

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysOauthClient> query, SysOauthClientVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getClientId()), SysOauthClient::getClientId, param.getClientId())
                .like(StrUtil.isNotBlank(param.getClientName()), SysOauthClient::getClientName, param.getClientName())
                .eq(StrUtil.isNotBlank(param.getClientType()), SysOauthClient::getClientType, param.getClientType())
                .eq(StrUtil.isNotBlank(param.getCheckCaptcha()), SysOauthClient::getCheckCaptcha, param.getCheckCaptcha())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysOauthClient::getStatus, param.getStatus());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysOauthClientVo getVoById(Long id) {
        SysOauthClientVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "OAuth客户端不存在");
        }
        return vo;
    }

    /**
     * 新增；带事务。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysOauthClient saveVo(SysOauthClientVo vo) {
        return super.saveVo(vo);
    }

    /**
     * 修改；带事务。密钥留空表示不改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(SysOauthClientVo vo) {
        if (StrUtil.isBlank(vo.getClientSecret())) {
            vo.setClientSecret(null);
        }
        return super.updateVoById(vo);
    }

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    @Override
    public void removeByIds(List<Long> ids) {
        deleteByIds(ids);
    }
}
