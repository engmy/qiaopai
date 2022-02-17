package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.service.LoginLogService;
import com.qixuan.common.entity.LoginLog;
import com.qixuan.common.utils.ISODateUtil;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import com.qixuan.common.utils.ServletUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class LoginLogServiceImpl implements LoginLogService
{
    @Resource
    private HttpServletRequest request;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getLoginLogList(Integer page, Integer limit, Map<String, String> params)
    {
        String collectionName = "login_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        Criteria criteria = new Criteria();
        if(StrUtil.isNotEmpty(params.get("ip")))
        {
            criteria.and("ip").regex(params.get("ip"));
        }
        if(StrUtil.isNotEmpty(params.get("username")))
        {
            criteria.and("username").regex(params.get("username"));
        }
        if(StrUtil.isNotEmpty(params.get("status")))
        {
            criteria.and("status").is(Integer.valueOf(params.get("status")));
        }
        String startDate = params.get("startDate");
        String endDate = params.get("endDate");
        if(StrUtil.isNotEmpty(startDate) && StrUtil.isEmpty(endDate))
        {
            collectionName = "login_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(startDate, "yyyy-MM-dd"));
            criteria.and("create_time").gte(ISODateUtil.dateToISODate(startDate));
        }
        if(StrUtil.isNotEmpty(endDate) && StrUtil.isEmpty(startDate))
        {
            collectionName = "login_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(endDate, "yyyy-MM-dd"));
            criteria.and("create_time").lte(ISODateUtil.dateToISODate(endDate));
        }
        if(StrUtil.isNotEmpty(startDate) && StrUtil.isNotEmpty(endDate))
        {
            collectionName = "login_log_" + new SimpleDateFormat("yyyyMM").format(DateUtil.parse(startDate, "yyyy-MM-dd"));
            criteria.andOperator(Criteria.where("create_time").gte(ISODateUtil.dateToISODate(startDate)), Criteria.where("create_time").lte(ISODateUtil.dateToISODate(endDate)));
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<LoginLog> loginLog = mongoTemplate.find(query,   LoginLog.class, collectionName);
        Long count = mongoTemplate.count(new Query(criteria), LoginLog.class, collectionName);
        return mongoUtil.pageHelper(count, loginLog);
    }

    @Async
    @Override
    public Boolean insertLoginLog(String username, Integer status, String params, String response)
    {
        String collectionName = "login_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(username);
        loginLog.setStatus(status);
        loginLog.setIp(ServletUtil.getClientIp(request));
        loginLog.setBrowser(ServletUtil.getBrowser(request).toString());
        loginLog.setOs(ServletUtil.getOsName(request));
        loginLog.setParams(params);
        loginLog.setResponse(response);

        LoginLog result = mongoTemplate.insert(loginLog, collectionName);
        if(result!=null)
        {
            return true;
        }else{
            return false;
        }
    }
}
