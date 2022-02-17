package com.qixuan.api.service;

import java.util.Date;

public interface StatsService
{
    // 生成当天的报表统计
    String generateReport(String fileName, Date mfgDate);

    String generateEmailText(String fileName, Date mfgDate);
}
