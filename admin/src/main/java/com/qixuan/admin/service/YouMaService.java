package com.qixuan.admin.service;

/**
 * 腾讯优码
 */
public interface YouMaService
{
    /**
     * 生成令牌
     * @return
     */
    String getToken();

    /**
     * 判断产品是否存在
     * @param jobNo 工单号
     * @param companyId 企业ID
     * @return
     */
    String viewProduct(String jobNo, String companyId);
}
