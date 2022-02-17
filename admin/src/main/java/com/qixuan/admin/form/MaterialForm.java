package com.qixuan.admin.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class MaterialForm implements Serializable
{
    @NotNull(message = "物料ID不能为空", groups ={ValidGroups.Update.class})
    private String id;

    // 产品编码
    @NotBlank(message = "产品编码不能为空！")
    private String skuNo;

    // 产品名称
    @NotBlank(message = "产品名称不能为空！")
    private String skuDesc = "";

    // 产品规格
    @NotBlank(message = "产品规格不能为空！")
    private String skuSpec;

    // 外箱里有几个单品
    private Integer carton2item = 0;

    // 内包装里有几个单品
    private Integer box2item = 0;

    // 外箱的单位
    @NotBlank(message = "外箱单位不能为空！")
    private String cartonUnit = "";

    // 内包装的单位
    private String boxUnit = "";

    // 单品的单位
    private String itemUnit = "";

    // 备用字段
    private String extend1 = "";

    // 备用字段
    private String extend2 = "";

    @NotNull(message = "托盘外箱数不能为空！")
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
