package com.qixuan.api.service;

import com.qixuan.api.form.WorkDocRelationForm;
import com.qixuan.api.vo.WorkDocDetailVo;
import com.qixuan.api.vo.WorkDocStatsVo;

public interface WorkDocRelationService
{
    /**
     * 上传工单关联
     * @param workDocRelationForm
     * @return
     */
    Boolean uploadWorkDocRelation(WorkDocRelationForm workDocRelationForm);

    /**
     * 工单统计查询
     * @param docNo
     * @return
     */
    WorkDocStatsVo getWorkDocStats(String docNo);

    /**
     * 工单统计明细
     * @param docNo
     * @return
     */
    WorkDocDetailVo getWorkDocDetail(String docNo);
}
