package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.admin.form.TaskForm;
import com.qixuan.admin.service.TaskService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.Task;
import com.qixuan.common.exception.CustomException;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService
{
	@Resource
	private HttpSession session;

	@Resource
	private MongoTemplate mongoTemplate;

	@Override
	public PageHelper getTaskList(Integer page, Integer limit, Map params)
	{
		Criteria criteria = Criteria.where("_id").ne("");
		if(StrUtil.isNotEmpty((String) params.get("task_name")))
		{
			criteria.and("task_name").regex(params.get("task_name").toString());
		}
		Query query = Query.query(criteria);
		query.with(Sort.by(Sort.Order.desc("_id")));
		MongoUtil mongoUtil = new MongoUtil();
		mongoUtil.start(page, limit, query);
		List<Task> taskList = mongoTemplate.find(query, Task.class);
		Long count = mongoTemplate.count(new Query(criteria), Task.class);
		return mongoUtil.pageHelper(count, taskList);
	}

	@Override
	public Boolean insertTask(TaskForm taskForm)
	{
		if(this.existTask(taskForm.getTaskName()))
		{
			throw new CustomException("任务名称已存在！");
		}
		if(this.existTaskey(taskForm.getTaskKey()))
		{
			throw new CustomException("调用方法已存在！");
		}
		Admin admin = (Admin) session.getAttribute("admin");
		Task task = new Task();
		BeanUtils.copyProperties(taskForm, task);
		task.setCreateUser(admin.getUsername());
		task.setUpdateUser(admin.getUsername());

		Task taskResult = mongoTemplate.insert(task);
		Boolean apiResult = this.taskApi("start", taskResult.getTaskId());
		if(taskResult!=null && apiResult)
		{
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean updateTask(TaskForm taskForm)
	{
		Admin admin = (Admin) session.getAttribute("admin");
		Query query = Query.query(Criteria.where("_id").is(taskForm.getTaskId()));
		Update update = new Update();

		Task task = mongoTemplate.findOne(query, Task.class);

		update.set("task_name",     taskForm.getTaskName());
		update.set("task_key",      taskForm.getTaskKey());
		update.set("cron",          taskForm.getCron());
		update.set("status",        taskForm.getStatus());
		update.set("remark",        taskForm.getRemark());
		update.set("task_name",     taskForm.getTaskName());
		update.set("update_user",   admin.getUsername());
		update.set("update_time", 	DateUtil.date());
		
		Boolean apiResult = true;
		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Task.class);

		if(!task.getStatus().equals(taskForm.getStatus()))
		{
			apiResult = this.taskApi(taskForm.getStatus()==1 ? "start" : "pause", taskForm.getTaskId());
		}else{
			if(taskForm.getStatus()==1)
			{
				apiResult = this.taskApi("reset", taskForm.getTaskId());
			}
		}

		if(updateResult!=null && apiResult)
		{
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean existTask(String taskName)
	{
		Query query = Query.query(Criteria.where("task_name").is(taskName));
		return mongoTemplate.exists(query, Task.class);
	}

	@Override
	public Boolean deleteTask(String taskId)
	{
		Boolean apiResult = this.taskApi("pause", taskId);
		Query query = Query.query(Criteria.where("_id").is(taskId));
		Long count = mongoTemplate.remove(query, Task.class).getDeletedCount();
		if(apiResult &&count > 0)
		{
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Task getTaskInfo(String taskId)
	{
		return mongoTemplate.findById(taskId, Task.class);
	}

	@Override
	public Boolean updateStatus(String taskId, Integer status)
	{
		Admin admin = (Admin) session.getAttribute("admin");
		Query query = Query.query(Criteria.where("_id").is(taskId));

		Update update = new Update();
		update.set("status", status);
		update.set("update_user", admin.getUsername());
		update.set("update_time", 	DateUtil.date());
		UpdateResult statusResult = mongoTemplate.updateFirst(query, update, Task.class);
		Boolean apiResult = this.taskApi(status==1 ? "start" : "pause", taskId);
		if(statusResult!=null && apiResult)
		{
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean existTaskey(String taskey)
	{
		Query query = Query.query(Criteria.where("task_key").is(taskey));
		return mongoTemplate.exists(query, Task.class);
	}

	/**
	 * 计划任务远程调用
	 */
	public Boolean taskApi(String method, String taskId)
	{
		HashMap params = new HashMap();
		params.put("task_id", taskId);
		String result = HttpRequest.post("http://127.0.0.1:6002/api/task/" + method).form(params).timeout(20000).execute().body();
		JSONObject jsonObject = new JSONObject(result);
		if(jsonObject.getStr("code").equals("200"))
		{
			return true;
		}else{
			return false;
		}
	}
}