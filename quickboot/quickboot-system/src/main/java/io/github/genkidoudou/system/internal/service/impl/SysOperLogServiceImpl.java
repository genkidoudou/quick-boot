package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.system.internal.entity.SysOperLog;
import io.github.genkidoudou.system.internal.mapper.SysOperLogMapper;
import io.github.genkidoudou.system.internal.service.ISysOperLogService;
import io.github.genkidoudou.system.internal.vo.SysOperLogVo;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现。仅查询。
 */
@Service
public class SysOperLogServiceImpl extends BaseVoServiceImpl<SysOperLogMapper, SysOperLog, SysOperLogVo>
        implements ISysOperLogService {

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysOperLog> query, SysOperLogVo param) {
        if (param == null) {
            query.orderByDesc(SysOperLog::getOperTime);
            return;
        }
        query.like(StrUtil.isNotBlank(param.getOperName()), SysOperLog::getOperName, param.getOperName())
                .like(StrUtil.isNotBlank(param.getOperUrl()), SysOperLog::getOperUrl, param.getOperUrl())
                .eq(StrUtil.isNotBlank(param.getRequestMethod()), SysOperLog::getRequestMethod, param.getRequestMethod())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysOperLog::getStatus, param.getStatus())
                .orderByDesc(SysOperLog::getOperTime);
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysOperLogVo getVoById(Long id) {
        SysOperLogVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "操作日志不存在");
        }
        return vo;
    }
}
