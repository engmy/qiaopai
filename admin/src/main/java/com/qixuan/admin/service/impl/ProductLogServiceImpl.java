package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.service.ProductLogService;
import com.qixuan.common.entity.ProductLog;
import com.qixuan.common.utils.ISODateUtil;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ProductLogServiceImpl implements ProductLogService
{
    @Resource
    private MongoTemplate mongoTemplate;

    private String collectionName = "product_log_" + new SimpleDateFormat("yyyyMM").format(new Date());

    @Override
    public PageHelper getProductLogList(Integer page, Integer limit, Map<String, String> params)
    {
        Criteria criteria = Criteria.where("_id").ne("");
        if(StrUtil.isNotEmpty(params.get("title")))
        {
            criteria.and("title").regex(params.get("title"));
        }
        if(StrUtil.isNotEmpty(params.get("status")))
        {
            criteria.and("status").is(Integer.valueOf(params.get("status")));
        }
        String startDate = params.get("startDate");
        String endDate = params.get("endDate");
        if(StrUtil.isNotEmpty(startDate) && StrUtil.isEmpty(endDate))
        {
            collectionName = "product_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(startDate, "yyyy-MM-dd"));
            criteria.and("create_time").gte(ISODateUtil.dateToISODate(startDate));
        }
        if(StrUtil.isNotEmpty(endDate) && StrUtil.isEmpty(startDate))
        {
            collectionName = "product_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(endDate, "yyyy-MM-dd"));
            criteria.and("create_time").lte(ISODateUtil.dateToISODate(endDate));
        }
        if(StrUtil.isNotEmpty(startDate) && StrUtil.isNotEmpty(endDate))
        {
            collectionName = "product_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(startDate, "yyyy-MM-dd"));
            criteria.andOperator(Criteria.where("create_time").gte(ISODateUtil.dateToISODate(startDate)), Criteria.where("create_time").lte(ISODateUtil.dateToISODate(endDate)));
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<ProductLog> productLog = mongoTemplate.find(query, ProductLog.class, collectionName);
        Long count = mongoTemplate.count(new Query(criteria), ProductLog.class, collectionName);
        return mongoUtil.pageHelper(count, productLog);
    }

    @Override
    public ProductLog getProductLogDetail(String logId)
    {
        return mongoTemplate.findById(logId, ProductLog.class, collectionName);
    }
}
