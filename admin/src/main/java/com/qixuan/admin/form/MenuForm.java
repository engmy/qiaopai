package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class MenuForm implements Serializable
{
    @NotNull(message = "菜单ID不能为空", groups ={ValidGroups.Update.class})
    private String menuId;

    @NotBlank(message = "菜单名称不能为空！")
    private String menuName;

    @NotBlank(message = "权限标识不能为空")
    private String permission;

    @NotBlank(message = "上级菜单不能为空！")
    private String parentId;

    @NotBlank(message = "菜单URL不能为空！")
    private String url;

    private Integer sort = 0;

    @NotNull(message = "菜单类型不能为空！")
    private Integer urlType;

    @NotNull(message = "菜单状态不能为空！")
    private Integer status;

    @NotBlank(message = "请选择菜单图标！")
    private String icon = "";

    private String remarks = "";
}