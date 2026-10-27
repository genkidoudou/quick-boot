package io.github.genkidoudou.system.internal.message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 把「直接用户 + 群组」展开成发送当时的收件人。示例，不参与编译。
 * <p>
 * 部门只出现在群组里。每个部门带「是否包含下级」。停用用户不进入结果。
 * 用户状态取 {@code sys_user.status}：{@code 0} 正常，{@code 1} 停用。
 * 部门是否停用不影响展开。部门已被删除时，这一行贡献零个用户，不因此失败整次发送。
 */
public final class RecipientResolver {

    /** 正常用户。与字典 user_status 的「正常」一致。 */
    public static final String USER_ENABLED = "0";

    private final Directory directory;

    public RecipientResolver(Directory directory) {
        this.directory = directory;
    }

    /**
     * @param directUserIds 消息上直接指定的用户。不存在或已停用的忽略
     * @param groups        已经确认存在的群组。不存在的群组要在调用前整次失败
     * @return 去重后的用户 id。顺序不作为验收条件
     */
    public List<Long> resolve(List<Long> directUserIds, List<GroupAudience> groups) {
        LinkedHashSet<Long> recipients = new LinkedHashSet<>();
        addActiveUsers(recipients, directUserIds);
        for (GroupAudience group : groups) {
            addActiveUsers(recipients, group.userIds());
            for (DeptTarget dept : group.depts()) {
                // 包含下级且部门已删除时，descendantIds 得到空列表，不能再拿空条件去查用户。
                List<Long> deptIds = dept.includeChildren()
                        ? descendantIds(dept.deptId())
                        : List.of(dept.deptId());
                if (deptIds.isEmpty()) {
                    continue;
                }
                addActiveUsers(recipients, directory.userIdsInDepts(deptIds));
            }
        }
        return List.copyOf(recipients);
    }

    /** 只收下存在且状态为正常的用户。 */
    private void addActiveUsers(Set<Long> recipients, List<Long> userIds) {
        for (Long userId : userIds) {
            if (userId != null && directory.isActiveUser(userId)) {
                recipients.add(userId);
            }
        }
    }

    /**
     * 本部门加上沿 parent_id 能走到的全部下级。
     * 部门 id 再次出现就停，避免部门树成环时死循环。部门不存在则返回空列表。
     */
    private List<Long> descendantIds(Long rootId) {
        if (!directory.deptExists(rootId)) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        ArrayDeque<Long> queue = new ArrayDeque<>();
        queue.add(rootId);
        while (!queue.isEmpty()) {
            Long current = queue.removeFirst();
            if (!seen.add(current)) {
                continue;
            }
            ids.add(current);
            queue.addAll(directory.childDeptIds(current));
        }
        return ids;
    }

    /** 一个群组在发送当时的受众。用户和部门都允许为空。 */
    public record GroupAudience(Long groupId, List<Long> userIds, List<DeptTarget> depts) {
    }

    /** 群组里的一行部门。includeChildren 为 false 时只取 dept_id 正好等于该部门的用户。 */
    public record DeptTarget(Long deptId, boolean includeChildren) {
    }

    /**
     * 用户和部门的查询口。落地时换成 system 模块已有的用户、部门 API。
     */
    public interface Directory {

        /** 用户存在且 status = 0。 */
        boolean isActiveUser(Long userId);

        /** 部门还在。已删除返回 false，调用方把它当成零个用户。 */
        boolean deptExists(Long deptId);

        /** 直接下级，不递归。 */
        List<Long> childDeptIds(Long parentId);

        /** dept_id 落在这些部门里的用户 id，停用用户也返回，由解析器再过滤。 */
        List<Long> userIdsInDepts(List<Long> deptIds);
    }
}
