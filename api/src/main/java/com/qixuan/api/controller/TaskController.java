package com.qixuan.api.controller;

import cn.hutool.core.util.ObjectUtil;
import com.qixuan.api.config.ScheduledConfig;
import com.qixuan.api.service.TaskService;
import com.qixuan.common.entity.Task;
import com.qixuan.common.utils.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotBlank;

@ApiIgnore
@RestController
@RequestMapping("api/task")
public class TaskController extends ScheduledConfig
{
    @Autowired
    private TaskService taskService;

    @PostMapping("start")
    public AjaxResult start(@NotBlank(message = "任务ID不能为空") String task_id)
    {
        Task task = taskService.getTaskInfo(task_id);
        if(ObjectUtil.isEmpty(task))
        {
            return AjaxResult.error("定时任务不存在");
        }
        if(start(task))
        {
            return AjaxResult.success("启动成功");
        }else{
            return AjaxResult.error("启动失败");
        }
    }

    @PostMapping("pause")
    public AjaxResult pause(@NotBlank(message = "任务ID不能为空") String task_id)
    {
        Task task = taskService.getTaskInfo(task_id);
        if(ObjectUtil.isEmpty(task))
        {
            return AjaxResult.error("定时任务不存在");
        }
        if(delete(task))
        {
            return AjaxResult.success("关闭成功");
        }else{
            return AjaxResult.error("关闭失败");
        }
    }

    @PostMapping("reset")
    public AjaxResult reset(@NotBlank(message = "任务ID不能为空") String task_id)
    {
        Task task = taskService.getTaskInfo(task_id);
        if(ObjectUtil.isEmpty(task))
        {
            return AjaxResult.error("定时任务不存在");
        }
        if(reset(task))
        {
            return AjaxResult.success("重启成功");
        }else{
            return AjaxResult.error("重启失败");
        }
    }
}
