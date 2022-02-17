package com.qixuan.api.service.impl;

import com.qixuan.api.service.PlcService;
import com.qixuan.common.entity.Call;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class PlcServiceImpl implements PlcService
{
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Call check(String pingVal)
    {
        mongoTemplate.dropCollection(Call.class);

        Call call = new Call();
        call.setPingVal(pingVal);
        call.setCreateUser("plc");
        call.setUpdateUser("plc");
        call.setCurrentTime(new Date());
        return mongoTemplate.insert(call);
    }
}