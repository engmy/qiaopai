package com.qixuan.common.service.impl;

import com.qixuan.common.entity.Config;
import com.qixuan.common.service.ConfigService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("common")
public class ConfingServiceImpl implements ConfigService
{
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String getConfigValueByKey(String configKey)
    {
        String value = "";
        Query query = Query.query(Criteria.where("config_key").is(configKey));
        Config config = mongoTemplate.findOne(query, Config.class);
        if(config!=null)
        {
            return config.getConfigValue();
        }else{
            return value;
        }
    }

    @Override
    public Map<String, String> getConfigMapByGroupId(Integer groupId)
    {
        Query query = Query.query(Criteria.where("group_id").is(groupId));
        List<Config> configList = mongoTemplate.find(query, Config.class);

        ConcurrentHashMap map = new ConcurrentHashMap();
        for(int i=0; i<configList.size(); i++)
        {
            Config config = configList.get(i);
            map.put(config.getConfigKey(), config.getConfigValue());
        }
        return map;
    }
}
