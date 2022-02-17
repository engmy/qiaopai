package com.qixuan.api.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class WorkDocForm implements Serializable
{
    private String id;

    // 生产工单
    @NotBlank(message = "生产工单不能为空！")
    private String docNo;

    // 产品编码
    @NotBlank(message = "产品编码不能为空！")
    private String skuNo;

    // 产品名称
    @NotBlank(message = "产品名称不能为空！")
    private String skuDesc;

    // 工单行号
    private Integer lineNo = 1;

    // 生产单位
    private String unit = "桶";

    // 包装级别
    private String level = "外箱";

    // 批号
    @NotBlank(message = "批号不能为空！")
    private String lotNo;

    // 生产日期
    @NotBlank(message = "生产日期不能为空！")
    private Date mfgDate;

    // 计划生产数量
    @NotNull(message = "计划数量不能为空！")
    private Integer reqQty;

    // 产线代码
    @NotBlank(message = "产线代码不能为空！")
    private String plineNo = "";

    // 单据状态(00待执行，10执行中，30已完成，40已上传 50上传异常)
    private String docStatus = "00";

    // 单据类型
    private String docCatalog = "成品生产任务单";

    // 备用字段
    private String extend1 = "";

    // 备用字段
    @NotBlank(message = "灌装单号不能为空！")
    private String extend2;
}
