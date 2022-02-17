package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 接口日志记录表
 *
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "api_log")
public class ApiLog extends BaseEntity
{
    /** 日志主键 */
    @Id
    private String operId;

    /**
     * 操作描述
     */
    private String title;

    /**
     * 操作时间
     */
    private Long startTime;

    /**
     * 消耗时间
     */
    private Integer spendTime;

    /**
     * 根路径
     */
    private String basePath;

    /**
     * URI
     */
    private String uri;

    /**
     * 请求方法
     */
    private String action;

    /**
     * 请求方式
     */
    private String method;

    /**
     * IP地址
     */
    private String ip;

    /** 操作地点 */
    private String location = "";

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求返回的结果
     */
    private String response;

    /** 操作状态（1正常 2异常） */
    private Integer status = 1;

    /** 错误消息 */
    private String errorMsg = "";
}