package com.qixuan.api.service;

import com.qixuan.common.entity.Call;

public interface PlcService
{
    // 通讯检测
    Call check(String pingVal);
}
