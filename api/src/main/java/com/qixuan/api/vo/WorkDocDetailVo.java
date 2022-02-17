package com.qixuan.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WorkDocDetailVo implements Serializable
{
    private String docNo;

    private String skuNo;

    private String lotNo;

    private Integer qty1;

    private List cartonCode;
}
