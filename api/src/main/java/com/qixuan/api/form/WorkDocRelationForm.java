package com.qixuan.api.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class WorkDocRelationForm implements Serializable
{
    @NotBlank(message = "工单号不能为空！")
    @ApiModelProperty(value = "工单号", required=true)
    @JsonProperty("doc_no")
    private String docNo;

    @NotBlank(message = "产品编码不能为空！")
    @ApiModelProperty(value = "产品编码", required=true)
    @JsonProperty("sku_no")
    private String skuNo;

    @NotBlank(message = "产品名称不能为空！")
    @ApiModelProperty(value = "产品名称", required=true)
    @JsonProperty("sku_desc")
    private String skuDesc;

    @ApiModelProperty(value = "工单行项目编号", hidden=true)
    private Integer lineNo = 1;

    @ApiModelProperty(value = "单据类型", hidden=true)
    private String docCatalog = "成品生产任务单";

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "生产日期不能为空！")
    @ApiModelProperty(value = "生产日期", required=true)
    @JsonProperty("mfg_date")
    private Date mfgDate;

    @ApiModelProperty(value = "计划数量", required=true)
    @NotNull(message = "计划数量不能为空！")
    @JsonProperty("req_qty")
    private Integer reqQty;

    @NotBlank(message = "生产批号不能为空！")
    @ApiModelProperty(value = "生产批号", required=true)
    @JsonProperty("lot_no")
    private String lotNo;

    @NotBlank(message = "产线代码不能为空！")
    @ApiModelProperty(value = "产线代码", required=true)
    @JsonProperty("pline_no")
    private String plineNo;

    @ApiModelProperty(value = "备用字段", hidden=true)
    private String extend1 = "";

    @NotBlank(message = "灌装单号不能为空！")
    @ApiModelProperty(value = "灌装单号", hidden=true)
    private String extend2 = "";

    @Valid
    @NotEmpty(message = "生产关联数据不能为空")
    @ApiModelProperty(value = "生产关联数据", required=true)
    private List<RelationForm> data;
}
