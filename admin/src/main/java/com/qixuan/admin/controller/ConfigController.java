package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.form.ConfigForm;
import com.qixuan.admin.form.ValidGroups;
import com.qixuan.admin.service.ConfigService;
import com.qixuan.common.entity.Config;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 物料管理
 */
@Controller
@RequestMapping(value = "/admin/config")
public class ConfigController
{
    @Autowired
    private ConfigService configService;

    /**
     * 配置列表
     */
    @SaCheckPermission("admin:config:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index()
    {
        return "admin/config/index";
    }

    /**
     * 配置列表
     */
    @ResponseBody
    @SaCheckPermission("admin:config:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult data(@RequestParam(value="group_id", defaultValue="1") Integer groupId)
    {
        List<Config> list = configService.getConfigList(groupId);
        return AjaxResult.success(list);
    }

    /**
     * 配置物料
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add()
    {
        return "admin/config/add";
    }

    /**
     * 新增配置
     */
    @Log(title = "配置管理", businessType = BusinessType.INSERT)
    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult insert(@Validated(ValidGroups.Insert.class) ConfigForm configForm)
    {
        Boolean result = false;
        if(configService.existConfig(configForm.getConfigKey()))
        {
            return AjaxResult.error("参数键名已存在!");
        }
        result = configService.insertConfig(configForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 更新配置
     */
    @Log(title = "配置管理", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult update(@RequestParam Map<String, Object> params)
    {
        Boolean result = configService.updateConfig(params);
        if(result)
        {
            return AjaxResult.success("保存成功");
        }else{
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 删除配置
     */
    @Log(title = "配置管理", businessType = BusinessType.DELETE)
    @ResponseBody
    @SaCheckPermission("admin:config:delete")
    @RequestMapping(value = "delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value="id") String id)
    {
        Boolean result = configService.deleteConfig(id);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }
}
