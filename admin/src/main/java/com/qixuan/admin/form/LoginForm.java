package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class LoginForm implements Serializable
{
    @NotBlank(message = "用户账号不能为空！")
    private String username;

    @NotBlank(message = "登录密码不能为空！")
    private String password;

    @NotBlank(message = "验证码不能为空！")
    private String captcha;
}
