package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 优码接口推送日志
 *
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_log")
public class ProductLog extends BaseEntity
{
    /** 日志主键 */
    @Id
    private String logId;

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
     * URl
     */
    private String url;

    /**
     * 请求方法
     */
    private String action;

    /**
     * 主机IP
     */
    private String ip;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求参数
     */
    private String paramsMd5;

    /**
     * 请求返回的结果
     */
    private String response;

    /** 操作状态（1正常 2异常） */
    private Integer status = 1;

    /** 错误消息 */
    private String errorMsg = "";

    /** 错误次数 */
    private Integer errorNum = 0;
}