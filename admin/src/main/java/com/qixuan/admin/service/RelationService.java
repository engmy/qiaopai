package com.qixuan.admin.service;

import com.qixuan.common.entity.Relation;
import com.qixuan.common.utils.PageHelper;

import java.util.Map;

public interface RelationService
{
    // 生产数据
    PageHelper getRelationList(Integer page, Integer limit, Map params);

    // 获取工单下个数
    Long getCartonCodeCout(String docNo);

    // 获取工单状态
    Relation getRelationInfo(String relationId);

    // 删除生产数据
    Boolean deleteRelation(String palletCodes);

    // 生产数据
    PageHelper getRelationData(Integer page, Integer limit, Map params);
}
