package com.qixuan.admin.service;

import com.qixuan.common.entity.OperLog;
import com.qixuan.common.utils.PageHelper;

import java.util.Map;

public interface OperLogService
{
    // 记录操作日志
    Boolean insertOperLog(OperLog operLog);

    // 获取操作记录
    PageHelper getOperLogList(Integer page, Integer limit, Map<String, String> params);

    // 操作日志详情
    OperLog getOperLogDetail(String logId);
}
