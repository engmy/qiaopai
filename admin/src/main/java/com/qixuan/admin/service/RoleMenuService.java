package com.qixuan.admin.service;

import com.qixuan.common.entity.RoleMenu;

import java.util.List;
import java.util.Set;

public interface RoleMenuService
{
	// 新增数据
	Boolean insertRoleMenu(String roleId, String menuIds);

	// 获取角色群贤
	List<RoleMenu> getRoleMenuList(String roleId);

	// 删除角色权限
	Boolean deleteRoleMenu(String roleId);

	// 获取角色权限菜单ID
	Set<String> getRoleMenuIds(String roleId, Boolean parent);

	// 获取角色权限菜单名称
	Set<String> getRoleMenuNames(String roleId);

	// 获取角色权限列表
	List<String> getRoleMenuPermissions(String roleId);
}
