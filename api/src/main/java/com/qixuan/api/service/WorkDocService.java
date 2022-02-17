package com.qixuan.api.service;

import com.qixuan.api.form.WorkDocForm;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.utils.PageHelper;

/**
 * 工单相关接口
 */
public interface WorkDocService
{
    // 查询工单列表
    PageHelper getWorkDocList(Integer page, Integer limit, String startTime, String endTime, String siteNo, String plineNo);

    // 更新工单状态
    Boolean updateWorkDocStatus(String docNo, String docStatus);

    // 新增工单
    Boolean insertWorkDoc(WorkDocForm workDocForm);

    // 工单信息查询
    WorkDoc getWorkDocInfo(String docNo, Integer type);

    // 查询工单个数
    Long getWorkDocCount(String docNo);

    // 判断工单编号是否存在
    Boolean existWorkDoc(String docNo);
}
