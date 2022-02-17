package com.qixuan.admin.service;

import cn.hutool.core.lang.tree.Tree;
import com.qixuan.admin.form.MenuForm;
import com.qixuan.common.entity.Menu;
import com.qixuan.common.utils.PageHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MenuService
{
    // 获取菜单
    PageHelper getMenuList(Integer page, Integer limit, Map<String, String> params);

    // 新增菜单
    Boolean insertMenu(MenuForm roleForm);

    // 更新菜单
    Boolean updateMenu(MenuForm roleForm);

    // 删除菜单
    Boolean deleteMenu(String menuId);

    // 获取菜单信息
    Menu getMenuInfo(String menuId);

    // 获全部菜单列表
    List<Tree<String>> getTreeMenuList();

    // 获取角色权限列表
    List<Tree<String>> getTreeRoleMenuList(String roleId);

    // 判断URL是否存在
    Boolean existMenuUrl(String url);

    // 判断权限节点是否存在
    Boolean existMenuPermission(String permission);

    // 判断是否有下级菜单
    Boolean existMenuChildren(String menuId);

    // 根据路径获取按钮
    Set<String> getMenuButtonList(String menuUrl);
}
