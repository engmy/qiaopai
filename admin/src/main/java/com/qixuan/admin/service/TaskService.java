package com.qixuan.admin.service;

import com.qixuan.admin.form.TaskForm;
import com.qixuan.common.entity.Task;
import com.qixuan.common.utils.PageHelper;

import java.util.Map;

public interface TaskService
{
	// 任务列表
	PageHelper getTaskList(Integer page, Integer limit, Map params);

	// 新增任务
	Boolean insertTask(TaskForm taskForm);

	// 更任务
	Boolean updateTask(TaskForm taskForm);

	// 判断任务是否存在
	Boolean existTask(String taskName);

	// 删除任务
	Boolean deleteTask(String taskId);

	// 获取用户详情
	Task getTaskInfo(String taskId);

	// 更任务状态
	Boolean updateStatus(String taskId, Integer status);

	// 判断任务是否存在
	Boolean existTaskey(String taskey);
}
