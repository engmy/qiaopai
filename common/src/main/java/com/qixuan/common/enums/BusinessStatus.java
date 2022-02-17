package com.qixuan.common.enums;

/**
 * 操作状态
 * 
 * @author ruoyi
 *
 */
public enum BusinessStatus
{
    SUCCESS(  1, "成功"),
    FAIL( 2, "失败");

    private final Integer code;
    private final String info;

    BusinessStatus(Integer code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
