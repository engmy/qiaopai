package com.qixuan.admin.service;

import com.qixuan.common.entity.Admin;

public interface AuthService
{
    /**
     * 用户登录接口
     * @param username
     * @param password
     * @return
     */
    Admin userLogin(String username, String password);

    /**
     * 用户登录校验
     * @param username
     * @param password
     * @return
     */
    Boolean authUserAndPassword(String username, String password);

    /**
     * 验证用户登录失败的次数
     * 错误 3 次 冻结20分钟
     * @param username
     * @return
     */
    String isErrLogion(String username);

}
