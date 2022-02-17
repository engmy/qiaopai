package com.qixuan.api.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class WorkDocStatsVo implements Serializable
{
    private String docNo;

    private String skuNo;

    private String lotNo;

    private Integer qty1;

    private Integer qty2;
}
