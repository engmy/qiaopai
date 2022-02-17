package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 角色权限表
 *
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "role_menu")
public class RoleMenu extends BaseEntity
{
    @Id
    private String id;

    /** 角色ID */
    private String roleId;

    /** 菜单ID */
    private String menuId;
}
