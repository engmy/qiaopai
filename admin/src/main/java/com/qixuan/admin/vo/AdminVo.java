package com.qixuan.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qixuan.common.utils.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户对象 admin
 *
 * @author huxg
 */
@Data
@Document(collection = "admin")
public class AdminVo extends BaseEntity
{
    /** 用户ID */
    @Id
    private String userId;

    /** 系统用户 */
    private Integer userType;

    /** 站点或部门 */
    private String siteNo;

    /** 用户账号 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phone;

    /** 用户性别 */
    private Integer sex;

    /** 用户头像 */
    private String avatar;

    /** 帐号状态（1正常 2停用） */
    private Integer status;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer isDelete;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    /** 角色Id */
    private String roleId;

    /** 错误次数
     * 错误 3 次以上 锁定20分钟 */
    private Integer errCount = 0;

    /** 用户角色 */
    private String roleName;
}
