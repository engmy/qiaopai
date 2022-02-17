package com.qixuan.admin.service;

import com.qixuan.admin.form.WorkDocForm;
import com.qixuan.admin.vo.WorkDocVo;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.utils.PageHelper;

import java.util.List;
import java.util.Map;

public interface WorkDocService
{
	// 工单列表
	PageHelper getWorkDocList(Integer page, Integer limit, Map params);

	// 所有列表
	List<WorkDocVo> getWorkDocList(Map params);

	// 新增工单
	Boolean insertWorkDoc(WorkDocForm workDocForm);

	// 编辑工单
	Boolean updateWorkDoc(WorkDocForm workDocForm);

	// 删除工单
	Boolean deleteWorkDoc(String id);

	// 判断工单编号是否存在
	Boolean existWorkDoc(String skuNo);

	// 获取工单详情
	WorkDoc getWorkDocInfo(String id);
}
