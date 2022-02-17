package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 登录日志记录表
 *
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "login_log")
public class LoginLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    private String operId;

    /** 用户账号 */
    private String username;

    /** 登录状态 1成功 2失败 */
    private Integer status;

    /** 登录IP地址 */
    private String ip;

    /** 登录地点 */
    private String loginLocation = "";

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求返回的结果
     */
    private String response;
}
