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

/**
 * 生产关系表
 *
 * @author huxg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "relation")
public class Relation extends BaseEntity
{
    @Id
    private String relationId;

    // 工单号
    @Indexed
    private String docNo;

    // 产品SKU
    @Indexed
    private String skuNo;

    // 产品名称
    private String skuDesc;

    // 工单行项目编号
    private String lineNo;

    // 单据类型
    private String docCatalog;

    // 生产日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date mfgDate;

    // 灌装单号
    private String fillingNo;

    // 批号
    private String lotNo;

    // 单品码
    private String itemCode;

    // 内包装码
    private String boxCode;

    // 外箱码，对应壳牌桶码TAC
    @Indexed(unique = true)
    private String cartonCode;

    // 托盘号，对应壳牌vcode
    private String palletCode;

    // 产线代码
    private String plineNo;

    // 工厂代码
    private String siteNo;

    // 工厂名称
    private String siteDesc;

    // 上传标志位
    private Integer uploadFlag;

    // 代表一个单据多次上传批次
    private String uploadGuid;

    // 上传时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    // 确认时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    // 生产时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mfgTime;

    // 班组代码
    private String teamNo;

    // 整批生产次序流水号
    private String sequenceId;

    // 虚拟托盘号，对应virtualcode
    private String extend1;

    // 备用字段
    private String extend2;

    // 软件版本号
    private String opVersion;

    // 组托时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date palletTime;

    // 异常情况
    private String remark;
}
