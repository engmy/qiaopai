package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RoleMenuForm implements Serializable
{
    @NotNull(message = "角色ID不能为空", groups ={ValidGroups.Update.class})
    private String roleId;

    @NotBlank(message = "角色名称不能为空！")
    private String roleName;

    @NotNull(message = "角色排序不能为空！")
    private Integer roleSort;

    @NotNull(message = "角色状态不能为空！")
    private Integer status = 1;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer isDelete = 0;

    /** 备注说明 */
    private String remarks = "";;

    @NotBlank(message = "请选择角色权限！")
    private String menuIds;
}