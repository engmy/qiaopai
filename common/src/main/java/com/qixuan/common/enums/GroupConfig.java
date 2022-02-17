package com.qixuan.common.enums;

/**
 * 参数分组
 */
public enum GroupConfig
{
    SYSTEM( 1, "系统配置"),
    YOUMA(  2, "优码中台"),
    COMPANY(3, "企业配置"),
    FACTORY(4, "工厂配置"),
    MAILBOX(5, "邮箱配置"),
    REPORT( 6, "报表配置");

    private final Integer code;
    private final String info;

    GroupConfig(Integer code, String info)
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
