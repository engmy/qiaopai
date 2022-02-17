package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "config")
public class Config extends BaseEntity
{
    @Id
    // 参数主键
    private String configId;

    // 参数名称
    private String configName;

    // 参数键名
    private String configKey;

    // 参数键值
    private String configValue;

    // 系统内置（Y是 N否）
    private String configType;

    // 配置分组
    private Integer groupId;

    // 备注说明
    private String remark;
}
