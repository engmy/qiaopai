package com.qixuan.api.service;

import com.qixuan.common.entity.ApiLog;

public interface ApiLogService
{
    // 接口日志记录
    Boolean insertApiLog(ApiLog apiLog);
}
