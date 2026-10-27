package io.github.genkidoudou.system.internal.message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 群组保存。示例，不参与编译。
 * <p>
 * 部门只放在群组里。同一个群组里，同一个用户一行，同一个部门一行。
 * 部门 id 必须还存在。群组可以暂时没有用户，也没有部门。
 */
public class GroupAdminExample {

    private final GroupRepository repository;
    private final DeptExists deptExists;

    public GroupAdminExample(GroupRepository repository, DeptExists deptExists) {
        this.repository = repository;
        this.deptExists = deptExists;
    }

    public long save(GroupForm form) {
        if (form.name() == null || form.name().isBlank()) {
            throw new IllegalArgumentException("群组名称必填");
        }
        List<Long> userIds = distinct(form.userIds());
        Map<Long, Boolean> depts = new LinkedHashMap<>();
        for (DeptChoice choice : form.depts()) {
            if (!deptExists.exists(choice.deptId())) {
                throw new IllegalArgumentException("部门不存在：" + choice.deptId());
            }
            // 同一部门后出现的那行覆盖前面的「是否包含下级」，保证只留一行。
            depts.put(choice.deptId(), choice.includeChildren());
        }
        List<DeptChoice> deptRows = new ArrayList<>();
        depts.forEach((deptId, includeChildren) -> deptRows.add(new DeptChoice(deptId, includeChildren)));
        if (form.id() == null) {
            return repository.insert(form.name().trim(), userIds, deptRows);
        }
        repository.update(form.id(), form.name().trim(), userIds, deptRows);
        return form.id();
    }

    private static List<Long> distinct(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().filter(Objects::nonNull).distinct().toList();
    }

    public record GroupForm(Long id, String name, List<Long> userIds, List<DeptChoice> depts) {
    }

    /** includeChildren 为 true 时，发送才带上全部下级部门的正常用户。 */
    public record DeptChoice(Long deptId, boolean includeChildren) {
    }

    public interface DeptExists {
        boolean exists(Long deptId);
    }

    public interface GroupRepository {
        long insert(String name, List<Long> userIds, List<DeptChoice> depts);

        void update(long id, String name, List<Long> userIds, List<DeptChoice> depts);
    }
}
