package com.qixuan.api.service;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.qixuan.common.entity.ProductLog;

public interface ProductLogService
{
    // 接口日志记录
    Boolean insertProductLog(JSONObject params, HttpResponse response);

    // 请求参数校验
    ProductLog getProductLogInfo(String params);

    // 更新接口日志
    Boolean updateProductLogInfo(ProductLog youMaLog);
}
