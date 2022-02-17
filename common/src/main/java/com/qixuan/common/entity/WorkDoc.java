package com.qixuan.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "workdoc")
public class WorkDoc extends BaseEntity
{
    @Id
    private String id;

    // 工单号
    @Indexed
    private String docNo;

    // 产品编码
    @Indexed
    private String skuNo;

    // 产品名称
    private String skuDesc;

    // 工单行号
    private Integer lineNo;

    // 生产单位
    private String unit;

    // 包装级别
    private String level;

    // 批号
    private String lotNo;

    // 工单来源（1、接口 2、手动创建）
    private Integer source;

    // 生产日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date mfgDate;

    // 计划生产数量
    private Integer reqQty;

    // 产线代码
    private String plineNo;

    // 工厂代码
    private String siteNo;

    // 工厂名称
    private String siteDesc;

    // 单据状态(00待执行，10执行中，30已完成，40已上传 50上传异常)
    private String docStatus;

    // 单据类型
    private String docCatalog;

    // 备用字段
    private String extend1;

    // 灌装单号
    private String extend2;

    // 整托盘有几个外箱
    private Integer pallet2carton;
}
