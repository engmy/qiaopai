package com.qixuan.api.service;

import com.qixuan.common.entity.Product;

import java.util.Map;

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
     * 环节数据上传
     * @param product 产品数据
     * @return
     */
    String uploadProduct(Product product);

    /**
     * 判断产品是否存在
     * @param jobNo 工单号
     * @param companyId 企业ID
     * @return
     */
    Map<String, Object> existProduct(String jobNo, String companyId);
}
