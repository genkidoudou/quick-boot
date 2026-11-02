package io.github.genkidoudou.msg.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.msg.internal.entity.MsgGroupMember;
import io.github.genkidoudou.msg.internal.mapper.MsgGroupMemberMapper;
import io.github.genkidoudou.msg.internal.service.IMsgGroupMemberService;
import io.github.genkidoudou.msg.internal.vo.MsgGroupMemberVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 群组成员服务实现。
 */
@Service
public class MsgGroupMemberServiceImpl extends BaseVoServiceImpl<MsgGroupMemberMapper, MsgGroupMember, MsgGroupMemberVo>
        implements IMsgGroupMemberService {

    /**
     * 用户。
     */
    private static final String TYPE_USER = "1";

    /**
     * 部门。
     */
    private static final String TYPE_DEPT = "2";

    /**
     * 外部人。
     */
    private static final String TYPE_EXTERNAL = "3";

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<MsgGroupMember> query, MsgGroupMemberVo param) {
        if (param == null) {
            return;
        }
        query.eq(param.getGroupId() != null, MsgGroupMember::getGroupId, param.getGroupId())
                .eq(StrUtil.isNotBlank(param.getMemberType()), MsgGroupMember::getMemberType, param.getMemberType())
                .eq(param.getUserId() != null, MsgGroupMember::getUserId, param.getUserId())
                .eq(param.getDeptId() != null, MsgGroupMember::getDeptId, param.getDeptId())
                .like(StrUtil.isNotBlank(param.getName()), MsgGroupMember::getName, param.getName());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public MsgGroupMemberVo getVoById(Long id) {
        MsgGroupMemberVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "群组成员不存在");
        }
        return vo;
    }

    /**
     * 新增；带事务。按成员类型检查必填列。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgGroupMember saveVo(MsgGroupMemberVo vo) {
        checkMember(vo);
        return super.saveVo(vo);
    }

    /**
     * 修改；带事务。按成员类型检查必填列。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(MsgGroupMemberVo vo) {
        checkMember(vo);
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

    @Override
    public List<MsgGroupMemberVo> listByGroupId(Long groupId) {
        return super.listVo(new LambdaQueryWrapper<MsgGroupMember>()
                .in(MsgGroupMember::getGroupId, groupId));
    }

    /**
     * 用户填 userId，部门填 deptId，外部人要有名称且手机或邮箱至少一项。
     *
     * @param vo 视图对象
     */
    private void checkMember(MsgGroupMemberVo vo) {
        String message = switch (vo.getMemberType()) {
            case TYPE_USER -> vo.getUserId() == null ? "用户未填写" : null;
            case TYPE_DEPT -> vo.getDeptId() == null ? "部门未填写" : null;
            case TYPE_EXTERNAL -> {
                boolean named = StrUtil.isNotBlank(vo.getName());
                boolean contact = StrUtil.isNotBlank(vo.getPhone()) || StrUtil.isNotBlank(vo.getEmail());
                yield named && contact ? null : "外部人要有名称，且手机或邮箱至少一项";
            }
            default -> "成员类型不正确";
        };
        if (message != null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, message);
        }
    }
}
