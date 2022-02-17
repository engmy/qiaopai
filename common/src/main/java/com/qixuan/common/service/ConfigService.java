package com.qixuan.common.service;

import java.util.Map;

public interface ConfigService
{
	// 获取配置
	String getConfigValueByKey(String configKey);

	// 所有配置
	Map<String, String> getConfigMapByGroupId(Integer groupId);
}
