package com.qixuan.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.service.ProductService;
import com.qixuan.common.entity.Product;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService
{
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getProductList(Integer page, Integer limit, Map params)
    {
        Criteria criteria = new Criteria();

        String jobNo  = (String) params.get("job_no");
        String status = (String) params.get("http_status");

        if(StrUtil.isNotEmpty(jobNo))
        {
            criteria.and("job_no").is(jobNo);
        }
        if(StrUtil.isNotEmpty(status))
        {
            criteria.and("http_status").is(Integer.parseInt(status));
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<Product> material = mongoTemplate.find(query, Product.class);

        Long count = mongoTemplate.count(new Query(criteria), Product.class);
        PageHelper pageHelper = mongoUtil.pageHelper(count, material);
        return pageHelper;
    }

    @Override
    public Integer getProductCartonNum(String docNo)
    {
        Query query = Query.query(Criteria.where("http_status").is(200).and("doc_no").is(docNo));
        List<Product> productList = mongoTemplate.find(query, Product.class);

        if(productList.size()==0) {
            return 0;
        }
        Integer cartonNum = 0;
        for(int i=0; i<productList.size(); i++)
        {
            Product product = productList.get(i);
            cartonNum += product.getCartonNum();
        }
        return cartonNum;
    }

    @Override
    public Product getProductInfo(String palletCode)
    {
        Query query = Query.query(Criteria.where("pallet_code").is(palletCode));
        return mongoTemplate.findOne(query, Product.class);
    }

    @Override
    public Boolean deleteProduct(String[] relationIds)
    {
        Query query = Query.query(Criteria.where("pallet_code").in(relationIds));
        Long count = mongoTemplate.remove(query, Product.class).getDeletedCount();
        if(count>0)
        {
            return true;
        }else{
            return false;
        }
    }
}
