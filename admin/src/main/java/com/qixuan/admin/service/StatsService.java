package com.qixuan.admin.service;

import com.qixuan.common.utils.PageHelper;

import java.util.Map;

public interface StatsService
{
    PageHelper getStatsList(Integer page, Integer limit, Map params);
}
