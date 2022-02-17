package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class TaskForm implements Serializable
{
    @NotNull(message = "任务ID不能为空", groups ={ValidGroups.Update.class})
    private String taskId;

    @NotBlank(message = "任务名称不能为空！")
    private String taskName;

    @NotBlank(message = "调用方法不能为空！")
    private String taskKey;

    @NotBlank(message = "Cron表达式不能为空！")
    private String cron;

    @NotNull(message = "任务状态不能为空！")
    private Integer status;

    private String remark = "";
}
