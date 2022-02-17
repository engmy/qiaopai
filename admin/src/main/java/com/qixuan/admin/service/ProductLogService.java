package com.qixuan.admin.service;

import com.qixuan.common.entity.ProductLog;
import com.qixuan.common.utils.PageHelper;

import java.util.Map;

public interface ProductLogService
{
    // 获取接口操作日志
    PageHelper getProductLogList(Integer page, Integer limit, Map<String, String> params);

    // 获取接口操作日志详情
    ProductLog getProductLogDetail(String logId);
}
