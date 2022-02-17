package com.qixuan.admin.service;

import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;

public interface ExcelService
{
    /**
     * 下载模板
     * @param fileName
     * @param response
     * @param workbook
     */
    void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook);
}
