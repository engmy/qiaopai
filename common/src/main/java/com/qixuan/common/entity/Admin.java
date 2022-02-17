package com.qixuan.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 用户对象 admin
 *
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "admin")
public class Admin extends BaseEntity
{
    /** 用户ID */
    @Id
    private String userId;

    /** 系统用户 */
    private Integer userType = 0;

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

    /** 密码 */
    private String password;

    /** 盐加密 */
    private String salt;

    /** 帐号状态（1正常 2停用） */
    private Integer status;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer isDelete;

    /** 最后登录IP */
    private String loginIp;

    /** 密码修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatePwdTime;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    /** 错误次数
     * 错误 3 次以上 锁定20分钟 */
    private Integer errCount;

    /** 角色Id */
    private String roleId;
}
