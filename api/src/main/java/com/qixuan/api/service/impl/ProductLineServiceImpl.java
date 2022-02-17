package com.qixuan.api.service.impl;

import com.qixuan.api.service.ProductLineService;
import com.qixuan.common.entity.ProductLine;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProductLineServiceImpl implements ProductLineService
{
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean existProductLine(String plineNo)
    {
        Query query = Query.query(Criteria.where("pline_no").is(plineNo));
        return mongoTemplate.exists(query, ProductLine.class);
    }
}
