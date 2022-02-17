package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class AdminForm implements Serializable
{
    /** 用户ID */
    @NotNull(message = "用户ID不能为空", groups ={ValidGroups.Update.class})
    private String userId;

    /** 站点或部门 */
    private String siteNo = "ZP001";

    /** 用户账号 */
    @NotBlank(message = "用户账号不能为空！")
    private String username;

    /** 系统用户 */
    private Integer userType = 0;

    /** 用户昵称 */
    @NotBlank(message = "用户昵称不能为空！")
    private String nickname;

    /** 用户邮箱 */
    private String email = "";

    /** 手机号码 */
    @NotBlank(message = "手机号码不能为空！")
    private String phone;

    /** 用户性别 */
    private Integer sex = 1;

    /** 密码 */
    private String password;

    /** 角色Id */
    @NotBlank(message = "请选择角色！")
    private String roleId;

    /** 用户头像 */
    private String avatar = "";

    /** 帐号状态（1正常 2停用） */
    private Integer status = 1;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer isDelete = 0;

    /** 最后登录IP */
    private String loginIp = "127.0.0.1";

    /** 错误次数
     * 错误 3 次以上 锁定20分钟 */
    private Integer errCount = 0;

    /** 最后登录时间 */
    private Date loginDate = new Date();
}
