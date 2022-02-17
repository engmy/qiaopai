package com.qixuan.admin.service;

import com.qixuan.admin.form.ConfigForm;
import com.qixuan.common.entity.Config;

import java.util.List;
import java.util.Map;

public interface ConfigService
{
	// 配置列表
	List<Config> getConfigList(Integer groupId);

	// 新增配置
	Boolean insertConfig(ConfigForm configForm);

	// 编辑配置
	Boolean updateConfig(Map<String, Object> params);

	// 删除配置
	Boolean deleteConfig(String id);

	// 判断配置
	Boolean existConfig(String configKey);

	// 获取配置详情
	Config getConfigInfo(String id);

	// 获取配置详情
	String getConfigByKey(String configKey);

	// 所有配置
	Map<String, String> getConfigMapByGroupId(Integer groupId);
}
