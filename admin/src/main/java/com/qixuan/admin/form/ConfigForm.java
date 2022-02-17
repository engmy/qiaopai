package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ConfigForm implements Serializable
{
    // 参数主键
    @NotNull(message = "配置ID不能为空", groups ={ValidGroups.Update.class})
    private String configId;

    // 参数名称
    @NotBlank(message = "参数名称不能为空！")
    private String configName;

    // 参数键名
    @NotBlank(message = "参数键名不能为空！")
    private String configKey;

    // 参数键值
    @NotBlank(message = "参数键值不能为空！")
    private String configValue;

    // 系统内置（Y是 N否）
    @NotBlank(message = "请选择参数类型！")
    private String configType;

    // 参数分组
    @NotNull(message = "请选择参数分组！")
    private Integer groupId;

    // 备注说明
    private String remark = "";
}
