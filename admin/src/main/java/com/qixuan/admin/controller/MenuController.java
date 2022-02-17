package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.form.MenuForm;
import com.qixuan.admin.form.ValidGroups;
import com.qixuan.admin.service.MenuService;
import com.qixuan.common.entity.Menu;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin/menu")
public class MenuController
{
    @Autowired
    private MenuService menuService;

    /**
     * 菜单管理
     */
    @SaCheckPermission("admin:menu:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/menu/index";
    }

    /**
     * 菜单列表
     */
    @ResponseBody
    @SaCheckPermission("admin:menu:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult list(@RequestParam(value="page", defaultValue="1") Integer page,
                           @RequestParam(value="limit", defaultValue="10") Integer limit,
                           @RequestParam Map params
    )
    {
        PageHelper list = menuService.getMenuList(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 新增菜单
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(ModelMap map)
    {
        map.put("treeList", menuService.getTreeMenuList());
        return "admin/menu/add";
    }

    /**
     * 编辑菜单
     */
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(@RequestParam(value="id") String id, ModelMap map)
    {
        if(StrUtil.isNotEmpty(id))
        {
            Menu menu = menuService.getMenuInfo(id);
            map.put("info", menu);
            if(!menu.getParentId().equals("0"))
            {
                Menu parent = menuService.getMenuInfo(menu.getParentId());
                map.put("menuName", parent.getMenuName());
            }else{
                map.put("menuName", "选择上级菜单");
            }
        }
        return "admin/menu/edit";
    }

    /**
     * 新增菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult insert(@Validated(ValidGroups.Insert.class) MenuForm menuForm)
    {
        Boolean result = menuService.insertMenu(menuForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 更新菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult update(@Validated(ValidGroups.Update.class) MenuForm menuForm)
    {
        Boolean result = menuService.updateMenu(menuForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 删除菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value="id") String menuId)
    {
        Boolean result = menuService.deleteMenu(menuId);
        if(result) {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 选择图标
     */
    @RequestMapping(value = "icon", method = RequestMethod.GET)
    public String icon(ModelMap map)
    {
        InputStream filePath = this.getClass().getResourceAsStream("/static/admin/font/icon.txt");
        List icons = IoUtil.readLines(filePath, "UTF-8", new ArrayList());
        map.put("icons", icons);
        return "admin/menu/icon";
    }

    @ResponseBody
    @RequestMapping(value = "tree", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult tree()
    {
        return AjaxResult.success(menuService.getTreeMenuList());
    }
}
