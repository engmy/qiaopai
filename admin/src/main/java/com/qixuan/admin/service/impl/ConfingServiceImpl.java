package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import com.qixuan.admin.form.ConfigForm;
import com.qixuan.admin.service.ConfigService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.Config;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("admin")
public class ConfingServiceImpl implements ConfigService
{
    @Resource
    private HttpSession session;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public List<Config> getConfigList(Integer groupId)
    {
        Query query = Query.query(Criteria.where("group_id").is(groupId));
        return mongoTemplate.find(query, Config.class);
    }

    @Override
    public Boolean insertConfig(ConfigForm configForm)
    {
        Admin admin = (Admin) session.getAttribute("admin");

        Config config = new Config();
        BeanUtils.copyProperties(configForm, config);

        config.setCreateUser(admin.getUsername());
        config.setUpdateUser(admin.getUsername());

        Config reuslt = mongoTemplate.insert(config);
        if(reuslt!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean updateConfig(Map<String, Object> params)
    {
        Admin admin = (Admin) session.getAttribute("admin");
        for(String key : params.keySet())
        {
            Query query  = Query.query(Criteria.where("config_key").is(key));
            Update update = new Update();
            update.set("config_value",  params.get(key));
            update.set("update_user",   admin.getUsername());
			update.set("update_time", 	DateUtil.date());
            mongoTemplate.updateFirst(query, update, Config.class);
        }
        return true;
    }

    @Override
    public Boolean deleteConfig(String id)
    {
        Query query = Query.query(Criteria.where("_id").is(id).and("config_type").is("N"));
        Long count = mongoTemplate.remove(query, Config.class).getDeletedCount();
        if(count>0)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean existConfig(String configKey)
    {
        Query query = Query.query(Criteria.where("config_key").is(configKey));
        return mongoTemplate.exists(query, Config.class);
    }

    @Override
    public Config getConfigInfo(String id)
    {
        return mongoTemplate.findById(id, Config.class);
    }

    @Override
    public String getConfigByKey(String configKey)
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
