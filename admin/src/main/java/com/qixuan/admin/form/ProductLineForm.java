package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ProductLineForm implements Serializable
{
    @NotNull(message = "产线ID不能为空", groups ={ValidGroups.Update.class})
    private String plineId;

    // 工厂代码
    @NotBlank(message = "产品编码不能为空！")
    private String siteNo;

    // 产线代码
    @NotBlank(message = "产线代码不能为空！")
    private String plineNo = "";

    // 产线描述
    private String plineDesc = "";
}
