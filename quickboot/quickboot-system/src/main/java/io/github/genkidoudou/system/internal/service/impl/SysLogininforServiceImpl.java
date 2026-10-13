package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysLogininfor;
import io.github.genkidoudou.system.internal.mapper.SysLogininforMapper;
import io.github.genkidoudou.system.internal.service.ISysLogininforService;
import io.github.genkidoudou.system.internal.vo.SysLogininforVo;
import org.springframework.stereotype.Service;

/**
 * 登录日志服务实现。查询 + 写入。
 */
@Service
public class SysLogininforServiceImpl extends BaseVoServiceImpl<SysLogininforMapper, SysLogininfor, SysLogininforVo>
        implements ISysLogininforService {

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysLogininfor> query, SysLogininforVo param) {
        if (param == null) {
            query.orderByDesc(SysLogininfor::getLoginTime);
            return;
        }
        query.like(StrUtil.isNotBlank(param.getUserName()), SysLogininfor::getUserName, param.getUserName())
                .like(StrUtil.isNotBlank(param.getIpAddr()), SysLogininfor::getIpAddr, param.getIpAddr())
                .like(StrUtil.isNotBlank(param.getTraceId()), SysLogininfor::getTraceId, param.getTraceId())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysLogininfor::getStatus, param.getStatus())
                .orderByDesc(SysLogininfor::getLoginTime);
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysLogininforVo getVoById(Long id) {
        SysLogininforVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "登录日志不存在");
        }
        return vo;
    }

    @Override
    public void record(SysLogininforVo vo) {
        if (vo == null) {
            return;
        }
        saveVo(vo);
    }
}
