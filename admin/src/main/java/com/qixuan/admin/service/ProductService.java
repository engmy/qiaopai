package com.qixuan.admin.service;

import com.qixuan.common.entity.Product;
import com.qixuan.common.utils.PageHelper;

import java.util.Map;

public interface ProductService
{
    // 获取产线
    PageHelper getProductList(Integer page, Integer limit, Map params);

    // 查询上传的桶数
    Integer getProductCartonNum(String docNo);

    // 查询托盘数据
    Product getProductInfo(String palletCode);

    // 删除优码产品数据
    Boolean deleteProduct(String[] relationIds);
}
