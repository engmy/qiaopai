package com.qixuan.admin.service;

import com.qixuan.common.entity.ApiLog;
import com.qixuan.common.utils.PageHelper;

import java.util.Map;

public interface ApiLogService
{
    // 获取接口操作日志
    PageHelper getApiLogList(Integer page, Integer limit, Map<String, String> params);

    // 获取接口操作日志详情
    ApiLog getApiLogDetail(String operId);
}
