package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.service.OperLogService;
import com.qixuan.common.entity.OperLog;
import com.qixuan.common.utils.ISODateUtil;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OperLogServiceImpl implements OperLogService
{
    @Resource
    private HttpSession session;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getOperLogList(Integer page, Integer limit, Map<String, String> params)
    {
        String collectionName = "oper_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        Criteria criteria = Criteria.where("_id").ne("");
        if(StrUtil.isNotEmpty(params.get("title")))
        {
            criteria.and("title").regex(params.get("title"));
        }
        if(StrUtil.isNotEmpty(params.get("status")))
        {
            criteria.and("status").is(Integer.valueOf(params.get("status")));
        }
        if(StrUtil.isNotEmpty(params.get("create_user")))
        {
            criteria.and("create_user").regex(params.get("create_user"));
        }
        if(StrUtil.isNotEmpty(params.get("business_type")))
        {
            criteria.and("business_type").is(Integer.valueOf(params.get("business_type")));
        }
        String startDate = params.get("startDate");
        String endDate = params.get("endDate");
        if(StrUtil.isNotEmpty(startDate) && StrUtil.isEmpty(endDate))
        {
            collectionName = "oper_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(startDate, "yyyy-MM-dd"));
            criteria.and("create_time").gte(ISODateUtil.dateToISODate(startDate));
        }
        if(StrUtil.isNotEmpty(endDate) && StrUtil.isEmpty(startDate))
        {
            collectionName = "oper_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(endDate, "yyyy-MM-dd"));
            criteria.and("create_time").lte(ISODateUtil.dateToISODate(endDate));
        }
        if(StrUtil.isNotEmpty(startDate) && StrUtil.isNotEmpty(endDate))
        {
            collectionName = "oper_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(startDate, "yyyy-MM-dd"));
            criteria.andOperator(Criteria.where("create_time").gte(ISODateUtil.dateToISODate(startDate)), Criteria.where("create_time").lte(ISODateUtil.dateToISODate(endDate)));
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<OperLog> operLogList = mongoTemplate.find(query, OperLog.class, collectionName);
        Long count = mongoTemplate.count(new Query(criteria), OperLog.class, collectionName);
        return mongoUtil.pageHelper(count, operLogList);
    }

    @Async
    @Override
    public Boolean insertOperLog(OperLog operLog)
    {
        String collectionName = "oper_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        OperLog reuslt = mongoTemplate.insert(operLog, collectionName);
        if(reuslt!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public OperLog getOperLogDetail(String logId)
    {
        String collectionName = "oper_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        return mongoTemplate.findById(logId, OperLog.class, collectionName);
    }
}
