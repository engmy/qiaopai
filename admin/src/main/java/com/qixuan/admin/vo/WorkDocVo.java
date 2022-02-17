package com.qixuan.admin.vo;

import com.qixuan.common.entity.WorkDoc;
import lombok.Data;

@Data
public class WorkDocVo extends WorkDoc
{
    // 实际生产数量
    private Integer qty1;

    //  已上传中台数量
    private Integer qty2;
}