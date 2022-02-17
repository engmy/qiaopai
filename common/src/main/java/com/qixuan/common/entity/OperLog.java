package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 操作日志记录表
 * 
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "oper_log")
public class OperLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    @Id
    private String operId;

    /** 操作模块 */
    private String title;

    /** 业务类型（0其它 0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据） */
    private Integer businessType;

    /** 请求方法 */
    private String action;

    /** 请求方式 */
    private String method;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    private Integer type;

    /** 请求uri */
    private String uri;

    /** 操作地址 */
    private String ip;

    /** 操作地点 */
    private String location = "";

    /** 请求参数 */
    private String params;

    /** 返回参数 */
    private String response;

    /** 操作状态（1正常 2异常） */
    private Integer status;

    /** 错误消息 */
    private String errorMsg = "";
}
