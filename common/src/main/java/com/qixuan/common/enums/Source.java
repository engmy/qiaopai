package com.qixuan.common.enums;

/**
 * 工单状态
 */
public enum Source
{
    API(1, "接口"), MANUAL(2, "手工维护");

    private final Integer code;
    private final String info;

    Source(Integer code, String info)
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
