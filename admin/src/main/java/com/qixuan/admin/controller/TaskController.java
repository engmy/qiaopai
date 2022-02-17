package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.form.TaskForm;
import com.qixuan.admin.form.ValidGroups;
import com.qixuan.admin.service.TaskService;
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

import java.util.Map;

@Controller
@RequestMapping(value = "/admin/task")
public class TaskController
{
    @Autowired
    private TaskService taskService;

    /**
     * 任务管理
     */
    @SaCheckPermission("admin:task:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params",   params);
        return "admin/task/index";
    }

    /**
     * 任务列表
     */
    @ResponseBody
    @SaCheckPermission("admin:task:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult data(@RequestParam(value="page", defaultValue="1") Integer page,
    @RequestParam(value="limit", defaultValue="10") Integer limit, @RequestParam Map params)
    {
        PageHelper list = taskService.getTaskList(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 新增用户
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add()
    {
        return "admin/task/add";
    }

    /**
     * 编辑任务
     */
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(@RequestParam(value="id") String id, ModelMap map)
    {
        if(StrUtil.isNotEmpty(id))
        {
            map.put("info", taskService.getTaskInfo(id));
        }
        return "admin/task/edit";
    }

    /**
     * 新增任务
     */
    @Log(title = "新增任务", businessType = BusinessType.INSERT)
    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult insert(@Validated(ValidGroups.Insert.class) TaskForm taskForm)
    {
        Boolean result = taskService.insertTask(taskForm);
        if(result) {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 更新任务
     */
    @Log(title = "更新任务", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult update(@Validated(ValidGroups.Update.class) TaskForm taskForm)
    {
        Boolean result = taskService.updateTask(taskForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 删除任务
     */
    @Log(title = "删除任务", businessType = BusinessType.DELETE)
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value="id") String taskId)
    {
        Boolean result = taskService.deleteTask(taskId);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 更新状态
     */
    @Log(title = "更新状态", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "change", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult change(@RequestParam(value="id") String taskId, @RequestParam(value="status") Integer status)
    {
        Boolean result = taskService.updateStatus(taskId, status);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }
}
