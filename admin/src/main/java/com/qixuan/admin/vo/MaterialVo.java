package com.qixuan.admin.vo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "material")
public class MaterialVo implements Serializable
{
    // 产品编码
    private String skuNo;

    // 产品名称
    private String skuDesc;

    // 整托盘有几个外箱
    private Integer pallet2carton;
}
