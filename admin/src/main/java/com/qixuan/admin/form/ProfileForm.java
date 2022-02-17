package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ProfileForm implements Serializable
{
    /** 用户ID */
    @NotNull(message = "用户ID不能为空", groups ={ValidGroups.Update.class})
    private String userId;

    /** 用户昵称 */
    @NotEmpty(message = "用户昵称不能为空！")
    private String nickname;

    /** 用户邮箱 */
    @NotEmpty(message = "邮箱不能为空！")
    private String email = "";

    /** 手机号码 */
    @NotEmpty(message = "手机号码不能为空！")
    private String phone;

    /** 用户性别 */
    @NotNull(message = "请选择用户性别！")
    private Integer sex = 1;
}
