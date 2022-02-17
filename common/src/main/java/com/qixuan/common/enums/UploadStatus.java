package com.qixuan.common.enums;

/**
 * 生产数据上传状态
 */
public enum UploadStatus
{
    WAITING(  0,   "待上传"),
    UPLOADING(100, "上传中"),
    Uploaded( 200, "已上传"),
    EXCEPTION(500, "上传异常");

    private final Integer code;
    private final String info;

    UploadStatus(Integer code, String info)
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
