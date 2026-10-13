package io.github.genkidoudou.system.internal.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.crypto.PasswordCodec;
import io.github.genkidoudou.common.crypto.PasswordCodecFactories;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.system.api.api.SysConfigApi;
import io.github.genkidoudou.system.api.vo.SysUserVo;
import io.github.genkidoudou.system.internal.entity.SysUser;
import io.github.genkidoudou.system.internal.mapper.SysUserMapper;
import io.github.genkidoudou.system.internal.service.ISysDeptService;
import io.github.genkidoudou.system.internal.service.ISysRoleService;
import io.github.genkidoudou.system.internal.service.ISysUserService;
import io.github.genkidoudou.system.internal.vo.SysDeptVo;
import io.github.genkidoudou.system.internal.vo.SysRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现。
 */
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl extends BaseVoServiceImpl<SysUserMapper, SysUser, SysUserVo>
        implements ISysUserService {

    private final ISysRoleService sysRoleService;

    private final ISysDeptService sysDeptService;

    private final SysConfigApi sysConfigApi;


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

    @Override
    public PageInfo<SysUserVo> pageVo(PageRequest<SysUserVo> pageRequest) {
        PageInfo<SysUserVo> pageVo = super.pageVo(pageRequest);

        List<SysUserVo> records = pageVo.getRecords();
        List<Long> deptIds = records.stream().map(SysUserVo::getDeptId).filter(Objects::nonNull).toList();
        List<SysDeptVo> sysDeptVos = sysDeptService.listByIds(deptIds);
        if (CollectionUtil.isNotEmpty(records)) {
            // 查询用户关联的角色id集合
            for (SysUserVo record : records) {
                List<SysRoleVo> sysRoleVoList = sysRoleService.listRoleByUserId(record.getId());
                record.setRoleNames(sysRoleVoList.stream().map(SysRoleVo::getRoleName).distinct().collect(Collectors.toList()));
                if (null != record.getDeptId()) {
                    record.setDeptName(sysDeptVos.stream().filter(a -> a.getId().equals(record.getDeptId())).findFirst().map(SysDeptVo::getDeptName).orElse(null));
                }
            }
        }
        return pageVo;
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

        // 查询关联的角色
        List<Long> roleIds = sysRoleService.listRoleIdByUserId(id);
        vo.setRoleIds(roleIds);
        return vo;
    }

    /**
     * 新增用户并加密密码。
     *
     * @param sysUserVo 用户视图
     * @return 持久化后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser saveVo(SysUserVo sysUserVo) {
        // 检查用户名不能重复
        long count = super.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, sysUserVo.getUserName()));
        if (count > 0) {
            throw new WarningException(10002, sysUserVo.getUserName());
        }
        if (StrUtil.isNotBlank(sysUserVo.getPhonenumber())) {
            // 检查手机号不能重复
            long count2 = super.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getPhonenumber, sysUserVo.getPhonenumber()));
            if (count2 > 0) {
                throw new WarningException(10003, sysUserVo.getPhonenumber());
            }
        }

        encodePassword(sysUserVo);
        // 密码过期时间

        String passwordValidityPeriod = sysConfigApi.getConfigValue("password_validity_period", "-1");
        if (passwordValidityPeriod.equals("-1")) {
            // 永久
            sysUserVo.setPasswordExpireTime(DateUtil.parse("2099-12-31", DatePattern.NORM_DATE_PATTERN));
        } else {
            DateTime dateTime = DateUtil.offsetDay(new Date(), Integer.valueOf(passwordValidityPeriod));
            sysUserVo.setPasswordExpireTime(dateTime);
        }
        SysUser sysUser = super.saveVo(sysUserVo);
        List<Long> roleIds =
                sysUserVo.getRoleIds();
        sysRoleService.bindUserRole(sysUser.getId(), roleIds);
        return sysUser;
    }

    /**
     * 修改用户；密码为空则不覆盖原密码。
     *
     * @param sysUserVo 用户视图
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(SysUserVo sysUserVo) {
        if (StrUtil.isNotBlank(sysUserVo.getPhonenumber())) {
            // 检查手机号不能重复
            long count2 = super.count(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getPhonenumber, sysUserVo.getPhonenumber())
                    .ne(SysUser::getId, sysUserVo.getId()));
            if (count2 > 0) {
                throw new WarningException(10003, sysUserVo.getPhonenumber());
            }
        }

        //修改的时候不允许修改密码
        sysUserVo.setPassword(null);
        sysRoleService.bindUserRole(sysUserVo.getId(), sysUserVo.getRoleIds());
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
        sysRoleService.deleteBindRole(ids);

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

    @Override
    public void resetPassword(Long userId) {
        // // 检查当前会话是否已通过二级认证，如未通过则抛出异常
        StpUtil.checkSafe("checkPassword");

        SysUser sysUser = super.getById(userId);
        if (null == sysUser) {
            throw new WarningException(10006);
        }
        SysUser param = new SysUser();
        param.setId(userId);
        String defaultPassword = sysConfigApi.getConfigValue("default_password", true);
        PasswordCodec passwordCodec = PasswordCodecFactories.get(CommonEnums.PASSWORD_CODEC_ENCODED.getValue());
        param.setPassword(passwordCodec.encrypt(defaultPassword));

        // 设置密码默认时间
        String passwordValidityPeriod = sysConfigApi.getConfigValue("password_validity_period", "-1");
        if (passwordValidityPeriod.equals("-1")) {
            // 永久
            param.setPasswordExpireTime(DateUtil.parse("2099-12-31", DatePattern.NORM_DATE_PATTERN));
        } else {
            DateTime dateTime = DateUtil.offsetDay(new Date(), Integer.parseInt(passwordValidityPeriod));
            param.setPasswordExpireTime(dateTime);
        }
        super.updateById(param);
    }

    @Override
    public void checkPassword(Long userId, String password) {
        SysUser sysUser = super.getById(userId);
        if (null == sysUser) {
            throw new WarningException(10006);
        }
        PasswordCodec passwordCodec = PasswordCodecFactories.get(CommonEnums.PASSWORD_CODEC_ENCODED.getValue());
        boolean matches = passwordCodec.matches(password, sysUser.getPassword());
        if (!matches) {
            throw new WarningException(7002);
        }
        // 比对成功，为当前会话打开二级认证，有效期为120秒
        StpUtil.openSafe("checkPassword", 120);

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
