package com.qixuan.admin.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class WorkDocForm implements Serializable
{
    @NotNull(message = "工单ID不能为空", groups ={ValidGroups.Update.class})
    private String id;

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
    @NotNull(message = "生产日期不能为空！")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date mfgDate;

    // 计划生产数量
    @NotNull(message = "计划数量不能为空！")
    private Integer reqQty;

    // 产线代码
    @NotBlank(message = "产线代码不能为空！")
    private String plineNo = "";

    // 单据状态(00待执行，10执行中，30已完成，40已上传)
    private String docStatus = "00";

    // 单据类型
    private String docCatalog = "成品生产任务单";

    // 备用字段
    private String extend1 = "";

    // 备用字段
    @NotBlank(message = "灌装单号不能为空！")
    private String extend2;

    // 整托盘有几个外箱
    private Integer pallet2carton;
}
