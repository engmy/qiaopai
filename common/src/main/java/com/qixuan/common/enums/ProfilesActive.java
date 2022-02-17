package com.qixuan.common.enums;

/**
 * 配置环境
 */
public enum ProfilesActive
{
    DEV(   "dev",  "开发环境"),
    TEST(  "test", "测试环境"),
    PRE(   "pre",  "灰度环境"),
    PRO(   "pro",  "生产数据");

    private final String code;
    private final String info;

    ProfilesActive(String code, String info)
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
