package com.qixuan.admin.service;

import com.qixuan.admin.form.ProductLineForm;
import com.qixuan.common.entity.ProductLine;
import com.qixuan.common.utils.PageHelper;

import java.util.List;
import java.util.Map;

public interface ProductLineService
{
    // 获取产线
    PageHelper getProductLineList(Integer page, Integer limit, Map params);

    // 产线搜素
    List<ProductLine> searchProductLineList();

    // 新增产线
    Boolean insertProductLine(ProductLineForm productLineForm);

    // 判断产线编码是否存在
    Boolean existProductLine(String plineNo);

    // 删除产线
    Boolean deleteProductLine(String roleId);
}
