package com.qixuan.api.service.impl;

import com.qixuan.api.service.MaterialService;
import com.qixuan.common.entity.Material;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService
{
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public HashMap getMaterialList()
    {
        Query query = new Query(new Criteria());
        List<Material> list = mongoTemplate.find(query, Material.class);
        Long count = mongoTemplate.count(new Query(new Criteria()), Material.class);

        HashMap map = new HashMap();
        map.put("list",  list);
        map.put("total", count);
        return map;
    }

    @Override
    public Boolean existMaterial(String skuNo)
    {
        Query query = Query.query(Criteria.where("sku_no").is(skuNo));
        return mongoTemplate.exists(query, Material.class);
    }

    @Override
    public Material getMaterialInfo(String skuNo)
    {
        Query query = Query.query(Criteria.where("sku_no").is(skuNo));
        return mongoTemplate.findOne(query, Material.class);
    }
}
