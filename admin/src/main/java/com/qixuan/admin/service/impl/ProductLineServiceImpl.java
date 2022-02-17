package com.qixuan.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.form.ProductLineForm;
import com.qixuan.admin.service.ProductLineService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.ProductLine;
import com.qixuan.common.exception.CustomException;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service
public class ProductLineServiceImpl implements ProductLineService
{
    @Resource
    private HttpSession session;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getProductLineList(Integer page, Integer limit, Map params)
    {
        // 产线查询
        Criteria criteria = Criteria.where("_id").ne("");
        if(StrUtil.isNotEmpty((String) params.get("pline_no")))
        {
            criteria.and("pline_no").regex(params.get("pline_no").toString());
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<ProductLine> material = mongoTemplate.find(query, ProductLine.class);

        Long count = mongoTemplate.count(new Query(criteria), ProductLine.class);
        PageHelper pageHelper = mongoUtil.pageHelper(count, material);
        return pageHelper;
    }

    @Override
    public List<ProductLine> searchProductLineList()
    {
        return mongoTemplate.findAll(ProductLine.class);
    }

    @Override
    public Boolean insertProductLine(ProductLineForm productLineForm)
    {
        Admin admin = (Admin) session.getAttribute("admin");
        if(this.existProductLine(productLineForm.getPlineNo()))
        {
            throw new CustomException("产线代码已存在");
        }

        ProductLine productLine = new ProductLine();
        BeanUtils.copyProperties(productLineForm, productLine);
        productLine.setCreateUser(admin.getUsername());
        productLine.setUpdateUser(admin.getUsername());

        ProductLine plineResult = mongoTemplate.insert(productLine);
        if(plineResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean existProductLine(String plineNo)
    {
        Query query = Query.query(Criteria.where("pline_no").is(plineNo));
        return mongoTemplate.exists(query, ProductLine.class);
    }

    @Override
    public Boolean deleteProductLine(String plineId)
    {
        Query query = Query.query(Criteria.where("_id").is(plineId));
        Long count = mongoTemplate.remove(query, ProductLine.class).getDeletedCount();
        if(count>0)
        {
            return true;
        }else{
            return false;
        }
    }
}
