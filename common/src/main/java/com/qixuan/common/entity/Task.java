package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 任务作业计划
 *
 * @author huxg
 */
@Data
@Document(collection = "task")
public class Task extends BaseEntity
{
    /** 任务ID */
    @Id
    private String taskId;

    /** 任务名称 */
    private String taskName;

    /** 调用方法 */
    private String taskKey;

    /** cron表达式 */
    private String cron;

    /** 任务状态 */
    private Integer status;

    /** 备注说明 */
    private String remark;
}
