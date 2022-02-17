package com.qixuan.api.service;

import com.qixuan.common.entity.Task;

import java.util.List;

public interface TaskService
{
    // 获取任务计划
    List<Task> getTaskList();

    // 获取计划任务详情
    Task getTaskInfo(String taskId);

    // 上传生产数据到优码
    Boolean pushProductTask();

    // 确认生产数据到优码
    Boolean pushRelationTask();
}
