package com.qixuan.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qixuan.common.utils.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product")
public class Product extends BaseEntity
{
    @Id
    private String productId;

    // 工控机单号
    private String docNo = "";

    // 实物托盘码
    private String palletCode;

    // 虚拟托盘号
    private String virtualCode;

    // 灌装单号
    private String fillingNo;

    // 环节code
    private String linkCode = "shellProduct";

    // 箱码外码关联文件
    private String file;

    // 企业id(测试环境2,正式环境3)
    private String companyId;

    // 码类型
    private String fileCodeType = "ESEQ_NUM";

    // 码类型
    private String codeType = "UCODE_VIRTUAL";

    // 优码工单号
    private String jobNo;

    // 接口推送状态
    private Integer httpStatus = 0;

    // 错误次数
    private Integer errorNum = 0;

    // 托盘上的桶数
    private Integer cartonNum = 0;

    // 数据条目
    private String dataJson = "";

    // 上传时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    // 确认时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    // 桶码位数
    private Integer cartonDigit;
}
