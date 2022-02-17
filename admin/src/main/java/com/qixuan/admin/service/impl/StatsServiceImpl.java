package com.qixuan.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.service.ProductService;
import com.qixuan.admin.service.RelationService;
import com.qixuan.admin.service.StatsService;
import com.qixuan.admin.vo.WorkDocVo;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatsServiceImpl implements StatsService
{
    @Resource
    private RelationService relationService;

    @Resource
    private ProductService productService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getStatsList(Integer page, Integer limit, Map params)
    {
        Criteria criteria = Criteria.where("_id").ne("");
        // 工单查询
        if(StrUtil.isNotEmpty((String) params.get("doc_no")))
        {
            criteria.and("doc_no").is(params.get("doc_no"));
        }
        // 产品查询
        if(StrUtil.isNotEmpty((String) params.get("sku_no")))
        {
            criteria.and("sku_no").is(params.get("sku_no"));
        }
        // 批号查询
        if(StrUtil.isNotEmpty((String) params.get("lot_no")))
        {
            criteria.and("lot_no").is(params.get("lot_no"));
        }

        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<WorkDoc> workDocList = mongoTemplate.find(query, WorkDoc.class);

        List<WorkDocVo> workDocStatsList = new ArrayList();
        if(workDocList.size()>0)
        {
            for (int i = 0; i < workDocList.size(); i++)
            {
                WorkDoc workDoc = workDocList.get(i);
                String docNo = workDoc.getDocNo();
                WorkDocVo workDocStats = new WorkDocVo();
                BeanUtils.copyProperties(workDoc, workDocStats);
                workDocStats.setQty1(relationService.getCartonCodeCout(docNo).intValue());
                workDocStats.setQty2(productService.getProductCartonNum(docNo));
                workDocStatsList.add(workDocStats);
            }
        }
        Long count = mongoTemplate.count(new Query(criteria), WorkDoc.class);
        PageHelper pageHelper = mongoUtil.pageHelper(count, workDocStatsList);
        return pageHelper;
    }
}
