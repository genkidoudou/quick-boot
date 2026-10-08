package io.github.genkidoudou.system.internal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.github.genkidoudou.common.exception.ErrorCodes;
import io.github.genkidoudou.common.exception.WarningException;
import io.github.genkidoudou.common.mybatisplus.BaseVoServiceImpl;
import io.github.genkidoudou.core.enums.CommonEnums;
import io.github.genkidoudou.system.api.vo.RouterVo;
import io.github.genkidoudou.system.internal.entity.SysMenu;
import io.github.genkidoudou.system.internal.enums.SysMenuEnums;
import io.github.genkidoudou.system.internal.mapper.SysMenuMapper;
import io.github.genkidoudou.system.internal.service.ISysMenuService;
import io.github.genkidoudou.system.api.vo.SysMenuVo;
import io.github.genkidoudou.system.internal.support.ControllerMenuParser;
import io.github.genkidoudou.system.internal.vo.SysMenuBatchRequestVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统菜单服务实现。
 */
@RequiredArgsConstructor
@Service
public class SysMenuServiceImpl extends BaseVoServiceImpl<SysMenuMapper, SysMenu, SysMenuVo>
        implements ISysMenuService {


    /**
     * 顶级菜单id
     *
     * @since 2026/10/4
     */

    public static final Long parentId = 0L;

    /** 布局组件 */
    private static final String LAYOUT = "Layout";
    /** 内嵌外链组件 */
    private static final String INNER_LINK = "InnerLink";
    /** 父级视图组件 */
    private static final String PARENT_VIEW = "ParentView";
    /** 否外链 */
    private static final String NO_FRAME = "0";

    /**
     * 按查询条件组装 Wrapper。
     * <p>菜单名称、权限标识模糊匹配；父 id、菜单类型、显示、状态精确匹配。
     *
     * @param query 查询包装器
     * @param param 查询参数
     */
    @Override
    public void applyQuery(LambdaQueryWrapper<SysMenu> query, SysMenuVo param) {
        if (param == null) {
            return;
        }
        query.like(StrUtil.isNotBlank(param.getMenuName()), SysMenu::getMenuName, param.getMenuName())
                .like(StrUtil.isNotBlank(param.getPerms()), SysMenu::getPerms, param.getPerms())
                .eq(param.getParentId() != null, SysMenu::getParentId, param.getParentId())
                .eq(StrUtil.isNotBlank(param.getMenuType()), SysMenu::getMenuType, param.getMenuType())
                .eq(StrUtil.isNotBlank(param.getVisible()), SysMenu::getVisible, param.getVisible())
                .eq(StrUtil.isNotBlank(param.getStatus()), SysMenu::getStatus, param.getStatus());
    }

    /**
     * 按主键查详情；不存在时抛出业务异常。
     *
     * @param id 主键
     * @return 详情
     */
    @Override
    public SysMenuVo getVoById(Long id) {
        SysMenuVo vo = super.getVoById(id);
        if (vo == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "菜单不存在");
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
    public SysMenu saveVo(SysMenuVo vo) {
        return super.saveVo(vo);
    }

    /**
     * 修改；带事务。
     *
     * @param vo 视图对象
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoById(SysMenuVo vo) {
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
    public List<SysMenuVo> listByRoleIds(List<Long> roleIds) {
//        if (CollectionUtil.isEmpty(roleIds)) {
//            return new ArrayList<>();
//        }
        return listVo(new LambdaUpdateWrapper<SysMenu>()
                .eq(SysMenu::getStatus, CommonEnums.STATUS_ENABLE.getValue()));
    }

    /**
     * 按条件查询菜单，再用 Hutool {@link TreeUtil} 组装为树。
     * <p>{@code parentId} 作为树根，不参与 SQL 过滤，以便挂上整棵子树。
     *
     * @param sysMenuVo 条件
     * @return 树形菜单
     * @since 2026/10/4
     */
    @Override
    public List<Tree<Long>> tree(SysMenuVo sysMenuVo) {
        SysMenuVo queryParam = sysMenuVo == null ? new SysMenuVo() : BeanUtil.copyProperties(sysMenuVo, SysMenuVo.class);
        Long rootId = queryParam.getParentId() == null ? parentId : queryParam.getParentId();
        queryParam.setParentId(null);

        LambdaQueryWrapper<SysMenu> query = new LambdaQueryWrapper<>();
        applyQuery(query, queryParam);
        query.orderByAsc(SysMenu::getOrderNum);

        List<SysMenuVo> menus = listVo(query);
        if (CollectionUtil.isEmpty(menus)) {
            return new ArrayList<>();
        }
        menus = fillAncestors(menus);

        return getTreeList(menus, rootId);
    }

    private static List<Tree<Long>> getTreeList(List<SysMenuVo> menus, Long rootId) {
        return TreeUtil.build(menus, rootId, (menu, tree) -> {
            tree.setId(menu.getId());
            tree.setParentId(menu.getParentId() == null ? rootId : menu.getParentId());
            tree.setName(menu.getMenuName());
            tree.setWeight(menu.getOrderNum());
            tree.putExtra("menuId", menu.getId());
            tree.putExtra("parentId", menu.getParentId());
            tree.putExtra("menuName", menu.getMenuName());
            tree.putExtra("menuType", menu.getMenuType());
            tree.putExtra("path", menu.getPath());
            tree.putExtra("routeName", menu.getRouteName());
            tree.putExtra("component", menu.getComponent());
            tree.putExtra("perms", menu.getPerms());
            tree.putExtra("icon", menu.getIcon());
            tree.putExtra("orderNum", menu.getOrderNum());
            tree.putExtra("query", menu.getQuery());
            tree.putExtra("isFrame", menu.getIsFrame());
            tree.putExtra("isCache", menu.getIsCache());
            tree.putExtra("visible", menu.getVisible());
            tree.putExtra("status", menu.getStatus());
            tree.putExtra("remark", menu.getRemark());
        });
    }

    /**
     * 把命中节点的祖先一路补到根，便于搜索后仍按原层级展示。
     *
     * @param matched 条件命中的菜单
     * @return 含祖先的节点集合
     */
    private List<SysMenuVo> fillAncestors(List<SysMenuVo> matched) {
        Map<Long, SysMenuVo> byId = new LinkedHashMap<>();
        for (SysMenuVo menu : matched) {
            if (menu.getId() != null) {
                byId.put(menu.getId(), menu);
            }
        }
        Set<Long> pending = new HashSet<>();
        for (SysMenuVo menu : matched) {
            Long pid = menu.getParentId();
            if (pid != null && !parentId.equals(pid) && !byId.containsKey(pid)) {
                pending.add(pid);
            }
        }
        while (!pending.isEmpty()) {
            List<SysMenu> parents = listByIds(pending);
            pending = new HashSet<>();
            if (CollectionUtil.isEmpty(parents)) {
                break;
            }
            for (SysMenu entity : parents) {
                if (entity.getId() == null || byId.containsKey(entity.getId())) {
                    continue;
                }
                SysMenuVo vo = BeanUtil.copyProperties(entity, SysMenuVo.class);
                byId.put(vo.getId(), vo);
                Long pid = vo.getParentId();
                if (pid != null && !parentId.equals(pid) && !byId.containsKey(pid)) {
                    pending.add(pid);
                }
            }
        }
        return new ArrayList<>(byId.values());
    }

    @Override
    public void sort(List<Long> menuIds, List<Integer> orderNums) {
        if (CollectionUtil.isEmpty(menuIds)) {
            return;
        }
        if (menuIds.size() != orderNums.size()) {
            throw new WarningException(400);
        }
        List<SysMenu> menus = new ArrayList<>();
        for (int i = 0; i < menuIds.size(); i++) {
            Long menuId = menuIds.get(i);
            Integer orderNum = orderNums.get(i);
            SysMenu sysMenu = new SysMenu();
            sysMenu.setId(menuId);
            sysMenu.setOrderNum(orderNum);
            menus.add(sysMenu);
        }

        super.updateBatchById(menus);
    }

    /**
     * 先保存菜单，再把按钮挂到该菜单下。
     *
     * @param menu    菜单
     * @param buttons 按钮
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMenuWithButtons(SysMenuVo menu, List<SysMenuVo> buttons) {
        if (menu == null) {
            throw WarningException.literal(ErrorCodes.Common.INVALID_PARAM, "菜单不能为空");
        }
        menu.setMenuType("C");
        if (menu.getParentId() == null) {
            menu.setParentId(parentId);
        }
        SysMenu saved = saveVo(menu);
        if (CollectionUtil.isEmpty(buttons)) {
            return;
        }
        int order = 1;
        for (SysMenuVo button : buttons) {
            if (button == null || StrUtil.isBlank(button.getMenuName())) {
                continue;
            }
            button.setId(null);
            button.setParentId(saved.getId());
            button.setMenuType("F");
            if (button.getOrderNum() == null) {
                button.setOrderNum(order);
            }
            order++;
            saveVo(button);
        }
    }

    /**
     * 从 Controller 源码解析菜单页与按钮。
     *
     * @param source 源码
     * @return 解析结果
     */
    @Override
    public SysMenuBatchRequestVo parseController(String source) {
        return ControllerMenuParser.parse(source);
    }

    @Override
    public List<RouterVo> selectMenuTreeByUserId(Long userId) {
        // TODO: 按 userId → 角色 → 菜单过滤；当前先返回全部启用目录/菜单
        List<SysMenu> list = super.list(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getMenuType,
                        SysMenuEnums.MENU_TYPE_DIR.getDictValue(),
                        SysMenuEnums.MENU_TYPE_MENU.getDictValue())
                .eq(SysMenu::getStatus, CommonEnums.STATUS_ENABLE.getValue())
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getOrderNum));
        List<SysMenuVo> menus = BeanUtil.copyToList(list, SysMenuVo.class);
        return buildMenus(buildMenuChildren(menus, parentId));
    }

    /**
     * 扁平菜单按 parentId 挂到 {@link SysMenuVo#getChildren()}。
     */
    private List<SysMenuVo> buildMenuChildren(List<SysMenuVo> menus, Long rootParentId) {
        if (CollectionUtil.isEmpty(menus)) {
            return new ArrayList<>();
        }
        Map<Long, List<SysMenuVo>> byParent = menus.stream()
                .collect(Collectors.groupingBy(m -> m.getParentId() == null ? rootParentId : m.getParentId(),
                        LinkedHashMap::new, Collectors.toList()));
        for (SysMenuVo menu : menus) {
            menu.setChildren(byParent.getOrDefault(menu.getId(), new ArrayList<>()));
        }
        return byParent.getOrDefault(rootParentId, new ArrayList<>());
    }

    /**
     * 将菜单树转为前端 vue-router 所需的 {@link RouterVo}。
     * <p>顶级目录 path 补 {@code /} 前缀，例如 {@code sys} → {@code /sys}。
     */
    private List<RouterVo> buildMenus(List<SysMenuVo> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenuVo menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new RouterVo.MetaVo(
                    menu.getMenuName(),
                    menu.getIcon(),
                    "1".equals(menu.getIsCache()),
                    menu.getPath()));
            List<SysMenuVo> cMenus = menu.getChildren();
            if (CollectionUtil.isNotEmpty(cMenus)
                    && SysMenuEnums.MENU_TYPE_DIR.getDictValue().equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(routeNameOf(menu.getPath(), menu.getRouteName()));
                children.setMeta(new RouterVo.MetaVo(
                        menu.getMenuName(),
                        menu.getIcon(),
                        "1".equals(menu.getIsCache()),
                        menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (isRoot(menu) && isInnerLink(menu)) {
                router.setMeta(new RouterVo.MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(INNER_LINK);
                children.setName(StrUtil.upperFirst(routerPath));
                children.setMeta(new RouterVo.MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    private String getRouteName(SysMenuVo menu) {
        if (isMenuFrame(menu)) {
            return StrUtil.EMPTY;
        }
        return routeNameOf(menu.getPath(), menu.getRouteName());
    }

    private String routeNameOf(String path, String routeName) {
        if (StrUtil.isNotBlank(routeName)) {
            return routeName;
        }
        return StrUtil.upperFirst(StrUtil.blankToDefault(path, StrUtil.EMPTY));
    }

    private String getRouterPath(SysMenuVo menu) {
        String routerPath = menu.getPath();
        if (!isRoot(menu) && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 一级目录：补绝对路径前缀，供 vue-router.addRoute
        if (isRoot(menu)
                && SysMenuEnums.MENU_TYPE_DIR.getDictValue().equals(menu.getMenuType())
                && NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + StrUtil.blankToDefault(menu.getPath(), StrUtil.EMPTY).replaceAll("^/+", "");
        } else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    private String getComponent(SysMenuVo menu) {
        String component = LAYOUT;
        if (StrUtil.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StrUtil.isEmpty(menu.getComponent()) && !isRoot(menu) && isInnerLink(menu)) {
            component = INNER_LINK;
        } else if (StrUtil.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = PARENT_VIEW;
        }
        return component;
    }

    /** 一级菜单（非目录）包一层 Layout */
    private boolean isMenuFrame(SysMenuVo menu) {
        return isRoot(menu)
                && SysMenuEnums.MENU_TYPE_MENU.getDictValue().equals(menu.getMenuType())
                && NO_FRAME.equals(menu.getIsFrame());
    }

    /** 非外链且 path 为 http(s) → InnerLink */
    private boolean isInnerLink(SysMenuVo menu) {
        return NO_FRAME.equals(menu.getIsFrame()) && isHttpPath(menu.getPath());
    }

    private boolean isParentView(SysMenuVo menu) {
        return !isRoot(menu) && SysMenuEnums.MENU_TYPE_DIR.getDictValue().equals(menu.getMenuType());
    }

    private boolean isRoot(SysMenuVo menu) {
        return menu.getParentId() == null || parentId.equals(menu.getParentId());
    }

    private boolean isHttpPath(String path) {
        return StrUtil.startWithIgnoreCase(path, "http://")
                || StrUtil.startWithIgnoreCase(path, "https://");
    }

    private String innerLinkReplaceEach(String path) {
        return StrUtil.replace(StrUtil.replace(StrUtil.replace(StrUtil.replace(
                        StrUtil.blankToDefault(path, StrUtil.EMPTY),
                        "http://", StrUtil.EMPTY),
                "https://", StrUtil.EMPTY),
                "www.", StrUtil.EMPTY),
                ".", "/");
    }

}
