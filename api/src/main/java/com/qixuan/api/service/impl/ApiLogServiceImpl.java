package com.qixuan.api.service.impl;

import com.qixuan.api.service.ApiLogService;
import com.qixuan.common.entity.ApiLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ApiLogServiceImpl implements ApiLogService
{
    @Resource
    private MongoTemplate mongoTemplate;

    @Async
    @Override
    public Boolean insertApiLog(ApiLog apiLog)
    {
        String collectionName = "api_log_" + new SimpleDateFormat("yyyyMM").format(new Date());
        Long spendTime = System.currentTimeMillis() - apiLog.getStartTime();
        apiLog.setCreateUser("plc");
        apiLog.setUpdateUser("plc");
        apiLog.setSpendTime(spendTime.intValue());
        ApiLog reuslt = mongoTemplate.insert(apiLog, collectionName);
        if(reuslt!=null)
        {
            return true;
        }else{
            return false;
        }
    }
}
