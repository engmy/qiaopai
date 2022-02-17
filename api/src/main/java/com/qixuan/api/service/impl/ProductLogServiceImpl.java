package com.qixuan.api.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.api.service.ProductLogService;
import com.qixuan.common.entity.ProductLog;
import com.qixuan.common.enums.BusinessStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ProductLogServiceImpl implements ProductLogService
{
    @Resource
    private MongoTemplate mongoTemplate;

    @Async
    @Override
    public Boolean insertProductLog(JSONObject params, HttpResponse response)
    {
        String collectionName = "product_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        ProductLog productlog = new ProductLog();
        Date startTime = (Date) params.get("uploadTime");
        Long spendTime = System.currentTimeMillis() - startTime.getTime();
        Integer errorNum = (Integer) params.get("errorNum") + 1;
        productlog.setStatus(BusinessStatus.SUCCESS.getCode());
        String body = response.body();

        // 返回XML处理
        if(!JSONUtil.isJson(body))
        {
            productlog.setErrorMsg(response.body());
            productlog.setStatus(BusinessStatus.FAIL.getCode());
        }

        // 返回Joon对象处理
        if(JSONUtil.isJsonObj(body))
        {
            if(200!=response.getStatus())
            {
                productlog.setErrorMsg(response.body());
                productlog.setStatus(BusinessStatus.FAIL.getCode());
            }
        }

        // 返回Joon数组处理
        if(JSONUtil.isJsonArray(body))
        {
            if(200!=response.getStatus() || 2==ObjectUtil.length(body))
            {
                productlog.setErrorMsg(response.body());
                productlog.setStatus(BusinessStatus.FAIL.getCode());
            }
        }

        params.remove("errorNum");
        params.remove("uploadTime");
        String paramsMd5 = SecureUtil.md5(JSONUtil.toJsonStr(params));
        ProductLog logInfo = getProductLogInfo(paramsMd5);
        productlog.setErrorNum(errorNum);
        productlog.setStartTime(startTime.getTime());

        if(ObjectUtil.isEmpty(logInfo))
        {
            productlog.setResponse(body);
            productlog.setCreateUser("plc");
            productlog.setUpdateUser("plc");
            productlog.setParamsMd5(paramsMd5);
            productlog.setSpendTime(spendTime.intValue());
            productlog.setParams(JSONUtil.toJsonStr(params));
            productlog.setTitle((String) params.get("title"));
            productlog.setUrl((String) params.get("url"));
            productlog.setIp((String) params.get("ip"));
            productlog.setAction((String) params.get("action"));
            productlog.setMethod((String) params.get("method"));
            ProductLog insertReuslt = mongoTemplate.insert(productlog, collectionName);
            if(insertReuslt!=null)
            {
                return true;
            }else{
                return false;
            }
        }else{
            productlog.setLogId(logInfo.getLogId());
            productlog.setSpendTime(logInfo.getSpendTime() > spendTime.intValue() ? logInfo.getSpendTime() : spendTime.intValue());
            return updateProductLogInfo(productlog);
        }
    }

    @Override
    public ProductLog getProductLogInfo(String paramsMd5)
    {
        String collectionName = "product_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        Query query = Query.query(Criteria.where("params_md5").is(paramsMd5));
        return mongoTemplate.findOne(query, ProductLog.class, collectionName);
    }

    @Override
    public Boolean updateProductLogInfo(ProductLog youMaLog)
    {
        String collectionName = "product_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        Query query = Query.query(Criteria.where("_id").is(youMaLog.getLogId()));
        Update update = new Update();
        update.set("error_num",  youMaLog.getErrorNum());
        update.set("spend_time", youMaLog.getSpendTime());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ProductLog.class, collectionName);

        if(updateResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }
}
