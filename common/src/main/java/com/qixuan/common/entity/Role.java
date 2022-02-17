package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 站点表
 *
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "role")
public class Role extends BaseEntity
{
    /** 角色ID */
    @Id
    private String roleId;

    /** 系统角色 */
    private Integer userType = 0;

    /** 角色名称 */
    private String roleName;

    /** 角色排序 */
    private Integer roleSort;

    /** 角色状态（1正常 2停用） */
    private Integer status;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer isDelete;

    /** 备注说明 */
    private String remarks;
}
