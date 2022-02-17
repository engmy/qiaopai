package com.qixuan.admin.service;

import com.qixuan.admin.form.MaterialForm;
import com.qixuan.admin.vo.MaterialVo;
import com.qixuan.common.entity.Material;
import com.qixuan.common.utils.PageHelper;

import java.util.List;
import java.util.Map;

public interface MaterialService
{
	// 物料列表
	PageHelper getMaterialList(Integer page, Integer limit, Map<String, String> params);

	// 物料搜索
	List<MaterialVo> searchMaterialList();

	// 新增物料
	Boolean insertMaterial(MaterialForm materialForm);

	// 编辑物料
	Boolean updateMaterial(MaterialForm materialForm);

	// 删除物料
	Boolean deleteMaterial(String id);

	// 判断物料编码是否存在
	Boolean existMaterial(String skuNo);

	// 获取物料详情
	Material getMaterialInfo(String id);

	// 获取物料信息
	Material getMaterialInfoBySkuNo(String skuNo);
}
