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
@Document(collection = "material")
public class Material extends BaseEntity
{
    @Id
    private String id;

    // 产品SKU
    private String skuNo;

    // 产品名称
    private String skuDesc;

    // 产品规格
    private String skuSpec;

    // 外箱里有几个单品
    private Integer carton2item;

    // 内包装里有几个单品
    private Integer box2item;

    // 外箱的单位
    private String cartonUnit;

    // 内包装的单位
    private String boxUnit;

    // 单品的单位
    private String itemUnit;

    // 数据源，1接口，2手工维护
    private Integer source;

    // 备用字段
    private String extend1;

    // 备用字段
    private String extend2;

    // 整托盘有几个外箱
    private Integer pallet2carton;

    // 产品英文名称
    private String midNameEng;

    // 产品中文名称
    private String midNameChn;

    // 厂家
    private String family;

    // 产品名称
    private String productName;

    // 产品组
    private String productGroup;

    // 产品组
    private String subGroup;

    // 包装
    private String packages;

    // 标签类型
    private String labellingType;

    // 标签类型
    private String sourceType;
}
