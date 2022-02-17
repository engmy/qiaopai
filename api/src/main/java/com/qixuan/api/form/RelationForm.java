package com.qixuan.api.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class RelationForm implements Serializable
{
    @ApiModelProperty(value = "单品码")
    @JsonProperty("item_code")
    private String itemCode = "";

    @ApiModelProperty(value = "内包装码")
    @JsonProperty("box_code")
    private String boxCode = "";

    @ApiModelProperty(value = "外箱码", required=true)
    @NotBlank(message = "外箱码不能为空！")
    @JsonProperty("carton_code")
    private String cartonCode;

    @ApiModelProperty(value = "托盘号", required=true)
    @NotBlank(message = "托盘号不能为空！")
    @JsonProperty("pallet_code")
    private String palletCode;

    @ApiModelProperty(value = "产线代码", required=true)
    @NotBlank(message = "产线代码不能为空！")
    @JsonProperty("pline_no")
    private String plineNo;

    @ApiModelProperty(value = "上传时间", hidden = true)
    private Date uploadTime;

    @ApiModelProperty(value = "上传标志位", hidden = true)
    private Integer uploadFlag = 0;

    @ApiModelProperty(value = "单据多次上传批次", hidden = true)
    private String uploadGuid = "";

    @ApiModelProperty(value = "班组代码", required=true)
    @NotBlank(message = "班组代码不能为空！")
    @JsonProperty("team_no")
    private String teamNo;

    @ApiModelProperty(value = "生产流水号", required=true)
    @NotBlank(message = "生产流水号不能为空！")
    @JsonProperty("sequence_id")
    private String sequenceId;

    @ApiModelProperty(value = "虚拟托盘号", required=true)
    @NotBlank(message = "虚拟托盘号不能为空！")
    private String extend1;

    @ApiModelProperty(value = "生产时间", required=true)
    @NotNull(message  = "生产日期不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("mfg_time")
    private Date mfgTime;

    @ApiModelProperty(value = "备注说明")
    private String remark = "";

    @ApiModelProperty(value = "备用字段")
    private String extend2 = "";

    @ApiModelProperty(value = "组托时间", required=true)
    @NotNull(message = "组托时间不能为空！")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("pallet_time")
    private Date palletTime;

    @ApiModelProperty(value = "软件版本号", required=true)
    @NotBlank(message = "软件版本号不能为空！")
    @JsonProperty("op_version")
    private String opVersion;
}
