package com.qixuan.api.service;

import com.qixuan.common.entity.Material;

import java.util.HashMap;

public interface MaterialService
{
	// 物料列表
	HashMap getMaterialList();

	// 判断物料编码是否存在
	Boolean existMaterial(String skuNo);

	// 获取物料信息
	Material getMaterialInfo(String skuNo);
}
