package com.qixuan.admin.service;

import com.qixuan.common.utils.PageHelper;

import java.util.Map;

public interface LoginLogService
{
    /**
     * 记录登录操作日志
     * @param username
     * @return
     */
    Boolean insertLoginLog(String username, Integer status, String params, String msg);

    /**
     * 获取接口操作日志
     * @param page
     * @param limit
     * @param params
     * @return
     */
    PageHelper getLoginLogList(Integer page, Integer limit, Map<String, String> params);
}
