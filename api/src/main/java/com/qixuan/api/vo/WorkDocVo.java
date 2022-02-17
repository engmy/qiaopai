package com.qixuan.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WorkDocVo implements Serializable
{
    // 生产日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date mfgDate;

    // 产线代码
    private String plineNo;

    // 工单号
    private String docNo;

    // 产品编码
    private String skuNo;

    // 包装规格
    private Integer pallet2carton;

    // 实际生产数量
    private Integer qty1;

    //  已上传中台数量
    private Integer qty2;

    // 批号
    private String lotNo;

    // 灌装单号
    private String extend2;
}