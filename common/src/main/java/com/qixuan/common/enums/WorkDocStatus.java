package com.qixuan.common.enums;

/**
 * 工单状态
 */
public enum WorkDocStatus
{
    EXECUTE(  "00", "待执行"),
    EXECUTING("10", "执行中"),
    COMPLETED("30", "已执行"),
    UPLOADED( "40", "已上传"),
    EXCEPTION("50", "上传异常");

    private final String code;
    private final String info;

    WorkDocStatus(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
