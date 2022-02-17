package com.qixuan.api.service;

import com.qixuan.api.form.RelationForm;
import com.qixuan.api.form.WorkDocForm;

import java.util.List;

public interface RelationService
{
    // 新增工单关联关系
    Boolean insertRelation(WorkDocForm workDocForm, List<RelationForm> relationFormList);

    // 获取工单下所有桶码
    List getCartonCodeList(String docNo);

    // 验证桶码是否有重复
    Boolean existCartonCode(String cartonCode);

    // 获取工单下个数
    Long getCartonCodeCout(String docNo);
}
