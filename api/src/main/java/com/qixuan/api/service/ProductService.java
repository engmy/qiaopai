package com.qixuan.api.service;

import com.qixuan.api.form.RelationForm;
import com.qixuan.api.form.WorkDocForm;
import com.qixuan.common.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService
{
    // 异步生成优码数据
    Boolean insertProduct(WorkDocForm workDocForm, List<RelationForm> relationFormList);

    // 查询未上传数据
    List<Product> getNoUploadProductList();

    // 查询10位未上传产品
    List<Product> getNoUploadProductList(Integer num);

    // 查询已上传未确认
    List<Product> getNoConfirmProductList();

    // 确认商品是否上传
    Map<String, Object> confirmProduct(String jobNo, String companyId);

    // 更新产品状态
    Boolean updateProductStatus(String productId, Integer status);

    // 写入回调工单编码
    Boolean updateProductJobNo(String productId, String jobNo);

    // 查询上传的桶数
    Integer getProductCartonNum(String docNo);

    // 更新错误次数
    Boolean updateProductErrorNum(String productId, Integer num);

    // 更新文件路径
    Boolean updateProductFilePath(String productId, String filePath);

    // 校验托盘桶数量
    Boolean checkCartonCodeNum(String fileName, Integer cartonNum);

    // 获取产品信息
    Product getProductInfoByJobNo(String jobNo);

    // 查询托盘总数
    Integer getPalletCodeNum(String docNo);

    // 查询已上传托盘数
    Integer getUploadPalletCodeNum(String docNo);

    // 校验上传托盘数
    Boolean checkPalletCodeNum(String docNo);
}