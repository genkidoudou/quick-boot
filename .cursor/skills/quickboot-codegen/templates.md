# 后端代码模板

生成时按本节模板落文件，把占位符替换为字段表中的值。模板结构来自 `SysUser` 全链路；**普通业务表去掉「仅 SysUser」标注的段落**。

## 占位符

| 占位符 | 含义 | 示例 |
|---|---|---|
| `{模块}` | 包模块名 | `system` |
| `{Name}` | 帕斯卡类名 | `SysNotice` |
| `{table}` | 表名 | `sys_notice` |
| `{resource}` | URL 资源段 | `notice` |
| `{映射}` | `@RequestMapping` | `sys/notice` 或 `{模块}/notice` |
| `{中文名}` | 中文业务名 | `通知公告` |
| `{Vo包}` | Vo 包路径 | 默认 `...internal.vo`；对外时 `...api.vo` |

业务字段：按字段表在 Entity / Vo 中追加属性；`applyQuery` 只对「查询」列写条件。

---

## 1. Entity

路径：`{模块}.internal.entity.{Name}`

```java
package io.github.genkidoudou.{模块}.internal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.genkidoudou.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * {中文名}实体，与表 {@code {table}} 对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("{table}")
public class {Name} extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    // TODO: 按字段表追加业务字段（单行 JavaDoc）；不要声明 BaseEntity 已有字段
}
```

关联表无完整审计/逻辑删除列时：去掉 `extends BaseEntity` 与 `@EqualsAndHashCode(callSuper = true)`，只声明实际列；`createBy`/`createTime` 可用 `@TableField(fill = FieldFill.INSERT)`。  
**注意：关联表默认走 §8，不要生成本节 Vo/Controller。**

---

## 2. Vo（默认 internal；仅业务表）

路径：`{模块}.internal.vo.{Name}Vo`（对外时改为 `{模块}.api.vo`）

```java
package io.github.genkidoudou.{模块}.internal.vo;

import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.core.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * {中文名}视图对象。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class {Name}Vo extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @NotNull(message = "{中文名}ID不能为空", groups = UpdateGroup.class)
    private Long id;

    // TODO: 按字段表追加字段 + JavaDoc
    // 必填示例：
    // @NotBlank(message = "xxx不能为空", groups = {AddGroup.class, UpdateGroup.class})
    // private String xxx;
}
```

**仅在需要时追加（参考 SysUserVo）：**

- 用户名：`@Pattern(regexp = PatternConstants.USER_NAME, ...)`
- 手机号：`@Pattern(regexp = PatternConstants.PHONE, ...)`
- 邮箱：`@Email`
- 密码（敏感、仅写入）：

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.genkidoudou.core.constants.PatternConstants;
import jakarta.validation.constraints.Pattern;

/**
 * 密码。仅写入，接口响应不序列化。
 */
@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
@NotBlank(message = "密码不能为空", groups = AddGroup.class)
@Pattern(regexp = PatternConstants.PASS_WORD,
        message = "密码至少8位，且须包含大小写字母、数字与特殊字符",
        groups = AddGroup.class)
private String password;
```

---

## 3. Mapper

路径：`{模块}.internal.mapper.{Name}Mapper`

```java
package io.github.genkidoudou.{模块}.internal.mapper;

import io.github.genkidoudou.common.mybatisplus.BaseBaseMapper;
import io.github.genkidoudou.{模块}.internal.entity.{Name};
import org.apache.ibatis.annotations.Mapper;

/**
 * {中文名} Mapper。
 */
@Mapper
public interface {Name}Mapper extends BaseBaseMapper<{Name}> {
}
```

---

## 4. I{Name}Service（先生成）

路径：`{模块}.internal.service.I{Name}Service`

```java
package io.github.genkidoudou.{模块}.internal.service;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.{模块}.internal.entity.{Name};
import io.github.genkidoudou.{模块}.internal.vo.{Name}Vo;

import java.util.List;

/**
 * {中文名}服务。
 */
public interface I{Name}Service {

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    PageInfo<{Name}Vo> pageVo(PageRequest<{Name}Vo> pageRequest);

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    {Name}Vo getVoById(Long id);

    /**
     * 新增。
     *
     * @param vo 视图对象
     * @return 持久化后的实体
     */
    {Name} saveVo({Name}Vo vo);

    /**
     * 修改。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    boolean updateVoById({Name}Vo vo);

    /**
     * 按主键批量删除。
     *
     * @param ids 主键列表
     */
    void removeByIds(List<Long> ids);
}
```

Vo 在 `api.vo` 时，把 import 改成 `io.github.genkidoudou.{模块}.api.vo.{Name}Vo`。

扩展方法仅在业务需要时追加（参考 SysUser 的 `findByUserName`）：

```java
    /**
     * 根据用户名查询用户视图（含密码，供登录校验）。
     *
     * @param username 用户名
     * @return 用户视图，不存在时返回 {@code null}
     */
    {Name}Vo findByUserName(String username);
```

---

## 5. {Name}ServiceImpl（后生成）

路径：`{模块}.internal.service.impl.{Name}ServiceImpl`

**标准 CRUD（无密码等特殊逻辑）：**

```java
package io.github.genkidoudou.{模块}.internal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.{模块}.internal.entity.{Name};
import io.github.genkidoudou.{模块}.internal.mapper.{Name}Mapper;
import io.github.genkidoudou.{模块}.internal.service.I{Name}Service;
import io.github.genkidoudou.{模块}.internal.vo.{Name}Vo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {中文名}服务实现。
 */
@Service
public class {Name}ServiceImpl extends BaseVoServiceImpl<{Name}Mapper, {Name}, {Name}Vo>
        implements I{Name}Service {

    /**
     * 按查询条件组装 Wrapper。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<{Name}> query, {Name}Vo param) {
        if (param == null) {
            return;
        }
        // TODO: 字段表「查询」列 — 字符串 like，其余 eq
        // query.like(StrUtil.isNotBlank(param.getXxx()), {Name}::getXxx, param.getXxx())
        //     .eq(param.getYyy() != null, {Name}::getYyy, param.getYyy());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public {Name}Vo getVoById(Long id) {
        {Name}Vo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "{中文名}不存在");
        }
        return vo;
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
```

说明：`pageVo` / `saveVo` / `updateVoById` 无额外逻辑时**不要**再写转调，直接继承基类即可（接口仍声明）。

**仅 SysUser 类业务需要时覆盖 saveVo / updateVoById（密码加密）：**

```java
    @Override
    public {Name} saveVo({Name}Vo vo) {
        encodePassword(vo);
        return super.saveVo(vo);
    }

    @Override
    public boolean updateVoById({Name}Vo vo) {
        if (StrUtil.isBlank(vo.getPassword())) {
            vo.setPassword(null);
        } else {
            encodePassword(vo);
        }
        return super.updateVoById(vo);
    }

    private void encodePassword({Name}Vo body) {
        PasswordCodec passwordCodec = PasswordCodecFactories.get(
                CommonEnums.PASSWORD_CODEC_ENCODED.getValue());
        body.setPassword(passwordCodec.encrypt(body.getPassword()));
    }
```

**扩展查询返回 Vo 示例：**

```java
    @Override
    public {Name}Vo findByUserName(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        return getVoOne(new LambdaQueryWrapper<{Name}>().eq({Name}::getUserName, username));
    }
```

---

## 6. Controller

路径：`{模块}.internal.controller.{Name}Controller`

```java
package io.github.genkidoudou.{模块}.internal.controller;

import io.github.genkidoudou.common.api.PageInfo;
import io.github.genkidoudou.common.api.PageRequest;
import io.github.genkidoudou.common.api.R;
import io.github.genkidoudou.common.validation.group.AddGroup;
import io.github.genkidoudou.common.validation.group.UpdateGroup;
import io.github.genkidoudou.{模块}.internal.service.I{Name}Service;
import io.github.genkidoudou.{模块}.internal.vo.{Name}Vo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * {中文名}管理接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("{映射}")
public class {Name}Controller {

    private final I{Name}Service {name}Service;

    /**
     * 分页查询。
     *
     * @param pageRequest 分页与查询条件
     * @return 分页结果
     */
    @PostMapping("page")
    public R<PageInfo<{Name}Vo>> page(@RequestBody PageRequest<{Name}Vo> pageRequest) {
        return R.ok({name}Service.pageVo(pageRequest));
    }

    /**
     * 按主键查询详情。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("{id}")
    public R<{Name}Vo> get(@PathVariable Long id) {
        return R.ok({name}Service.getVoById(id));
    }

    /**
     * 新增。
     *
     * @param body 表单数据
     */
    @PostMapping("add")
    public R<Void> add(@RequestBody @Validated(AddGroup.class) {Name}Vo body) {
        {name}Service.saveVo(body);
        return R.ok();
    }

    /**
     * 修改。
     *
     * @param body 表单数据
     */
    @PostMapping("update")
    public R<Void> update(@RequestBody @Validated(UpdateGroup.class) {Name}Vo body) {
        {name}Service.updateVoById(body);
        return R.ok();
    }

    /**
     * 批量删除。
     *
     * @param ids 主键列表
     */
    @PostMapping("remove")
    public R<Void> remove(@RequestBody List<Long> ids) {
        {name}Service.removeByIds(ids);
        return R.ok();
    }
}
```

其中 `{name}` 为 `{Name}` 的小驼峰，如 `SysNotice` → `sysNotice`。系统模块 `{映射}` = `sys/{resource}`。

禁止 `@PutMapping` / `@DeleteMapping`。Controller 只做校验与转发。

---

## 7. 对外 Api（仅跨模块时生成）

### `{Name}Api`

路径：`{模块}.api.api.{Name}Api`

```java
package io.github.genkidoudou.{模块}.api.api;

import io.github.genkidoudou.{模块}.api.vo.{Name}Vo;

/**
 * {中文名}对外 API。
 */
public interface {Name}Api {

    /**
     * 根据业务键查询（按实际改方法名与参数）。
     *
     * @param key 业务键
     * @return 视图对象，不存在时返回 {@code null}
     */
    {Name}Vo findByXxx(String key);
}
```

### `{Name}ApiImpl`

路径：`{模块}.internal.api.impl.{Name}ApiImpl`

```java
package io.github.genkidoudou.{模块}.internal.api.impl;

import io.github.genkidoudou.{模块}.api.api.{Name}Api;
import io.github.genkidoudou.{模块}.api.vo.{Name}Vo;
import io.github.genkidoudou.{模块}.internal.service.I{Name}Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * {@link {Name}Api} 实现。
 */
@Service
@RequiredArgsConstructor
public class {Name}ApiImpl implements {Name}Api {

    private final I{Name}Service {name}Service;

    /**
     * 根据业务键查询。
     *
     * @param key 业务键
     * @return 视图对象，不存在时返回 {@code null}
     */
    @Override
    public {Name}Vo findByXxx(String key) {
        return {name}Service.findByXxx(key);
    }
}
```

ApiImpl **直接委托** Service，不要再 `BeanUtil.copyProperties`。此时 Vo 须在 `api.vo`。

---

## 8. 关联表模板（默认：无 Vo / 无独立 CRUD）

适用于 `sys_user_role`、`sys_role_menu` 等中间表。占位符额外约定：

| 占位符 | 含义 | 示例 |
|---|---|---|
| `{Left}` / `{left}` | 左端实体 / 小驼峰 | `User` / `user` |
| `{Right}` / `{right}` | 右端实体 / 小驼峰 | `Role` / `role` |
| `{leftId}` / `{rightId}` | 外键属性名 | `userId` / `roleId` |
| `{getLeftId}` / `{getRightId}` | 外键 getter 方法引用名 | `getUserId` / `getRoleId` |
| `{setLeftId}` / `{setRightId}` | 外键 setter | `setUserId` / `setRoleId` |

### 8.1 Entity（不继承 BaseEntity）

路径：`{模块}.internal.entity.{Name}`

```java
package io.github.genkidoudou.{模块}.internal.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * {中文名}实体，与表 {@code {table}} 对应。
 * <p>关联表无完整审计/逻辑删除列时不继承 {@code BaseEntity}。
 */
@Data
@TableName("{table}")
public class {Name} implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID。 */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 创建人。 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 左端外键。 */
    private Long {leftId};

    /** 右端外键（列名异常时加 @TableField）。 */
    private Long {rightId};
}
```

表无 `create_by`/`create_time` 则删掉对应字段。列名如 `role_Id` 写成 `@TableField("role_Id") private Long roleId`。

### 8.2 Mapper

同 §3，无 XML。

### 8.3 绑定 Service（无 Vo，不继承 BaseVoServiceImpl）

优先把方法做进**主资源** `ISys{Left}Service`；或单独薄接口供主 Service 调用。

```java
package io.github.genkidoudou.{模块}.internal.service;

import java.util.List;

/**
 * {中文名}绑定服务（关联表，无 Vo）。
 */
public interface I{Name}Service {

    /**
     * 按左端主键查询右端 id 列表。
     *
     * @param {leftId} 左端主键
     * @return 右端 id 列表
     */
    List<Long> list{Right}IdsBy{Left}Id(Long {leftId});

    /**
     * 覆盖左端与右端的关联（先删后插；空列表表示清空）。
     *
     * @param {leftId}  左端主键
     * @param {right}Ids 右端 id 列表
     */
    void replace{Right}Ids(Long {leftId}, List<Long> {right}Ids);
}
```

```java
package io.github.genkidoudou.{模块}.internal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.genkidoudou.{模块}.internal.entity.{Name};
import io.github.genkidoudou.{模块}.internal.mapper.{Name}Mapper;
import io.github.genkidoudou.{模块}.internal.service.I{Name}Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {中文名}绑定服务实现。
 */
@Service
public class {Name}ServiceImpl extends ServiceImpl<{Name}Mapper, {Name}>
        implements I{Name}Service {

    /**
     * 按左端主键查询右端 id 列表。
     *
     * @param {leftId} 左端主键
     * @return 右端 id 列表
     */
    @Override
    public List<Long> list{Right}IdsBy{Left}Id(Long {leftId}) {
        if ({leftId} == null) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<{Name}>().eq({Name}::{getLeftId}, {leftId}))
                .stream()
                .map({Name}::{getRightId})
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 覆盖左端与右端的关联。
     *
     * @param {leftId}  左端主键
     * @param {right}Ids 右端 id 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replace{Right}Ids(Long {leftId}, List<Long> {right}Ids) {
        remove(new LambdaQueryWrapper<{Name}>().eq({Name}::{getLeftId}, {leftId}));
        if (CollectionUtil.isEmpty({right}Ids)) {
            return;
        }
        List<{Name}> rows = {right}Ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(rid -> {
                    {Name} row = new {Name}();
                    row.{setLeftId}({leftId});
                    row.{setRightId}(rid);
                    return row;
                })
                .collect(Collectors.toList());
        saveBatch(rows);
    }
}
```

生成时把 `{getLeftId}` 换成 `getUserId` 等真实方法名，方法引用写成 `{Name}::getUserId`（整段替换，不要留套娃占位符）。

主资源 HTTP 示例（写在主 Controller，**不是**关联表自己的 Controller）：

```java
    /**
     * 查询已绑定的右端 id 列表。
     */
    @GetMapping("{id}/{right}-ids")
    public R<List<Long>> list{Right}Ids(@PathVariable Long id) {
        return R.ok({name}Service.list{Right}IdsBy{Left}Id(id));
    }

    /**
     * 覆盖绑定。
     */
    @PostMapping("{id}/{right}-ids")
    public R<Void> replace{Right}Ids(@PathVariable Long id, @RequestBody List<Long> {right}Ids) {
        {name}Service.replace{Right}Ids(id, {right}Ids);
        return R.ok();
    }
```

---

## 生成检查清单

### 业务表

1. 先 `I{Name}Service`，再 `{Name}ServiceImpl extends BaseVoServiceImpl`
2. Controller 注入接口；`AddGroup` / `UpdateGroup`
3. 方法名：`pageVo` / `getVoById` / `saveVo` / `updateVoById` / `removeByIds`
4. 无 PUT/DELETE；有 JavaDoc
5. 默认 Vo 在 `internal.vo`；仅跨模块才写 `api`

### 关联表

1. 仅 Entity + Mapper + 绑定 Service（§8）
2. **无 Vo、无独立 Controller、无前端、无 api**
3. Entity 按实有列建模；不误用 `BaseVoServiceImpl`
4. 绑定通过主资源接口或主 Service 调用
5. 仅用户点名「关联表独立 CRUD」时才改走业务表清单
