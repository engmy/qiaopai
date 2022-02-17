package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.form.RoleMenuForm;
import com.qixuan.admin.form.ValidGroups;
import com.qixuan.admin.service.RoleMenuService;
import com.qixuan.admin.service.RoleService;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("admin/role")
public class RoleController
{
    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 角色管理
     */
    @SaCheckPermission("admin:role:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/role/index";
    }

    /**
     * 角色列表
     */
    @ResponseBody
    @SaCheckPermission("admin:role:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult list(@RequestParam(value="page", defaultValue="1") Integer page,
    @RequestParam(value="limit", defaultValue="10") Integer limit, @RequestParam Map params)
    {
        PageHelper list = roleService.getRoleList(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 新增角色
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add()
    {
        return "admin/role/add";
    }

    /**
     * 编辑角色
     */
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(@RequestParam(value="id") String roleId, Model map)
    {
        if(StrUtil.isNotEmpty(roleId))
        {
            Set<String> menuIds = roleMenuService.getRoleMenuIds(roleId, false);
            Set<String> menuTexts = roleMenuService.getRoleMenuNames(roleId);
            if(menuIds.size()>0)
            {
                map.addAttribute("menuIds",   String.join(",", menuIds));
                map.addAttribute("menuTexts", String.join(",", menuTexts));
            }else{
                map.addAttribute("menuIds",  "");
                map.addAttribute("menuTexts","选择角色权限");
            }
            map.addAttribute("info", roleService.getRoleInfo(roleId));
        }
        return "admin/role/edit";
    }

    /**
     * 新增角色
     */
    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult insert(@Validated(ValidGroups.Insert.class) RoleMenuForm roleMenuForm)
    {
        Boolean result = roleService.insertRole(roleMenuForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 保存表单数据
     */
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult update(@Validated(ValidGroups.Update.class) RoleMenuForm roleMenuForm)
    {
        Boolean result = roleService.updateRole(roleMenuForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 删除角色
     */
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value="id") String roleId)
    {
        Boolean result = roleService.deleteRole(roleId);
        if(result) {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }
}
