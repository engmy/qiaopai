package com.qixuan.common.entity;

import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 产线管理
 *
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_line")
public class ProductLine extends BaseEntity
{
    @Id
    private String plineId;

    // 工厂代码
    private String siteNo;

    // 产线代码
    private String plineNo;

    // 产线描述
    private String plineDesc;
}
