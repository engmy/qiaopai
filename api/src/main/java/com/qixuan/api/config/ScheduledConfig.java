package com.qixuan.api.config;

import com.qixuan.api.service.TaskService;
import com.qixuan.common.entity.Task;
import com.qixuan.common.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@EnableScheduling
public class ScheduledConfig implements SchedulingConfigurer
{
    protected static Logger logger = LoggerFactory.getLogger(ScheduledConfig.class);
    private static Map<String, ScheduledFuture<?>> scheduledFutureMap = new HashMap<>();

    @Resource
    private TaskService taskService;

    public ThreadPoolTaskScheduler taskScheduler()
    {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.setPoolSize(256);
        return threadPoolTaskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
    {
        List<Task> taskList = taskService.getTaskList();
        if(taskList.size()>0)
        {
            for (Task task : taskList)
            {
                try {
                    start(task);
                } catch (Exception e)
                {
                    logger.error("定时任务启动错误:" + task.getTaskKey() + ";" + e.getMessage());
                }
            }
        }
    }

    private static Runnable getRunnable(Task task)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    Object obj = SpringUtil.getBean("com.qixuan.api.service.TaskService");
                    Method method = obj.getClass().getMethod(task.getTaskKey());
                    method.invoke(obj);
                } catch (InvocationTargetException e)
                {
                    logger.error("定时任务启动错误，反射异常:" + task.getTaskKey()+ ";"+ e.getMessage());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        };
    }

    private static Trigger getTrigger(Task task)
    {
        return new Trigger()
        {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext)
            {
                CronTrigger trigger = new CronTrigger(task.getCron());
                Date nextExec = trigger.nextExecutionTime(triggerContext);
                return nextExec;
            }
        };
    }

    /**
     * 启动定时任务
     * @param task
     * @param
     */
    public Boolean start(Task task)
    {
        ScheduledFuture<?> scheduledFuture = taskScheduler().schedule(getRunnable(task), getTrigger(task));
        scheduledFutureMap.put(task.getTaskId(), scheduledFuture);
        if(scheduledFutureMap.size()>0)
        {
            logger.info("启动定时任务：" + task.getTaskKey() + " 成功");
            return true;
        }else{
            logger.info("启动定时任务：" + task.getTaskKey() + " 失败");
            return false;
        }
    }

    /**
     * 删除定时任务
     * @param task
     */
    public Boolean delete(Task task)
    {
        ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(task.getTaskId());
        if(scheduledFuture != null && !scheduledFuture.isCancelled())
        {
            scheduledFuture.cancel(Boolean.TRUE);
        }
        scheduledFutureMap.remove(task.getTaskId());
        if(scheduledFuture.isCancelled())
        {
            logger.info("删除定时任务：" + task.getTaskKey() + " 成功");
            return true;
        }else{
            logger.info("删除定时任务：" + task.getTaskKey() + " 失败");
            return false;
        }
    }

    /**
     * 修改任务计划
     * @param task
     * @param
     */
    public Boolean reset(Task task)
    {
        if(delete(task) && start(task))
        {
            logger.info("修改定时任务: " + task.getTaskKey() + " 成功");
            return true;
        }else{
            logger.info("修改定时任务：" + task.getTaskKey() + " 失败");
            return false;
        }
    }
}