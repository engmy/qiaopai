package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EditPassForm implements Serializable
{
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotBlank(message = "新密码不能为空")
    private String newpass;

    @NotBlank(message = "请输入重输新密码")
    private String renewpass;
}
