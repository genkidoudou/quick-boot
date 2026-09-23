package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.crypto.PasswordCodec;
import io.github.genkidoudou.common.crypto.PasswordCodecFactories;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.core.entity.enums.CommonEnums;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.system.internal.entity.SysUser;
import io.github.genkidoudou.system.internal.mapper.SysUserMapper;
import io.github.genkidoudou.system.internal.service.ISysUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统用户服务实现。
 */
@Service
public class SysUserServiceImpl extends BaseVoServiceImpl<SysUserMapper, SysUser, SysUserVo>
        implements ISysUserService {

    /**
     * 按查询条件组装 Wrapper。
     * <p>用户名、昵称、邮箱、手机号模糊匹配；部门、用户类型、性别、状态精确匹配。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysUser> query, SysUserVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getUserName()), SysUser::getUserName, param.getUserName())
                .like(StrUtil.isNotBlank(param.getNickName()), SysUser::getNickName, param.getNickName())
                .like(StrUtil.isNotBlank(param.getEmail()), SysUser::getEmail, param.getEmail())
                .like(StrUtil.isNotBlank(param.getPhonenumber()), SysUser::getPhonenumber, param.getPhonenumber())
                .eq(param.getDeptId() != null, SysUser::getDeptId, param.getDeptId())
                .eq(StrUtil.isNotBlank(param.getUserType()), SysUser::getUserType, param.getUserType())
                .eq(StrUtil.isNotBlank(param.getSex()), SysUser::getSex, param.getSex())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysUser::getStatus, param.getStatus());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysUserVo getVoById(Long id) {
        SysUserVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "用户不存在");
        }
        return vo;
    }

    /**
     * 新增用户并加密密码。
     *
     * @param sysUserVo 用户视图
     * @return 持久化后的实体
     */
    @Override
    public SysUser saveVo(SysUserVo sysUserVo) {
        encodePassword(sysUserVo);
        return super.saveVo(sysUserVo);
    }

    /**
     * 修改用户；密码为空则不覆盖原密码。
     *
     * @param sysUserVo 用户视图
     * @return 是否成功
     */
    @Override
    public boolean updateVoById(SysUserVo sysUserVo) {
        if (StrUtil.isBlank(sysUserVo.getPassword())) {
            sysUserVo.setPassword(null);
        } else {
            encodePassword(sysUserVo);
        }
        return super.updateVoById(sysUserVo);
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

    /**
     * 根据用户名查询用户视图。
     *
     * @param username 用户名
     * @return 用户视图，不存在时返回 {@code null}
     */
    @Override
    public SysUserVo findByUserName(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        return getVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username));
    }

    /**
     * 按登录同一套算法加密密码。
     *
     * @param body 含明文密码的用户 Vo
     */
    private void encodePassword(SysUserVo body) {
        PasswordCodec passwordCodec = PasswordCodecFactories.get(CommonEnums.PASSWORD_CODEC_ENCODED.getValue());
        body.setPassword(passwordCodec.encrypt(body.getPassword()));
    }
}
