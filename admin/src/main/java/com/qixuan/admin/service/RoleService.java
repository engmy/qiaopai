package com.qixuan.admin.service;

import com.qixuan.admin.form.RoleMenuForm;
import com.qixuan.common.entity.Role;
import com.qixuan.common.utils.PageHelper;

import java.util.List;
import java.util.Map;

/**
 * @author EDZ
 */
public interface RoleService
{
    /**
     * 获取角色
     * @param page
     * @param limit
     * @param params
     * @return
     */
    PageHelper getRoleList(Integer page, Integer limit, Map params);

    /**
     * 新增角色
     * @param roleMenuForm
     * @return
     */
    Boolean insertRole(RoleMenuForm roleMenuForm);

    /**
     * 更新角色
     * @param roleMenuForm
     * @return
     */
    Boolean updateRole(RoleMenuForm roleMenuForm);

    /**
     *  删除角色
     * @param roleId
     * @return
     */
    Boolean deleteRole(String roleId);

    /**
     * 获取角色信息
     * @param roleId
     * @return
     */
    Role getRoleInfo(String roleId);

    /**
     * 获取所有角色列表
     * @return
     */
    List<Role> getRoleList();

    /**
     * 获取所有角色列表 过滤掉系统管理员权限
     * @return
     */
    List<Role> getRoleListNeSys();

    /**
     * 判断角色是否存在
     * @param roleName
     * @return
     */
    Boolean existRole(String roleName);
}
