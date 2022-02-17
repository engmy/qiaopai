package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.admin.form.MaterialForm;
import com.qixuan.admin.service.MaterialService;
import com.qixuan.admin.vo.MaterialVo;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.Material;
import com.qixuan.common.enums.Source;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service
public class MaterialServiceImpl implements MaterialService
{
    @Resource
    private HttpSession session;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getMaterialList(Integer page, Integer limit, Map<String, String> params)
    {
        Criteria criteria = Criteria.where("_id").ne("");
        if(StrUtil.isNotEmpty(params.get("sku_no")))
        {
            criteria.and("sku_no").regex(params.get("sku_no"));
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<Material> material = mongoTemplate.find(query, Material.class);

        Long count = mongoTemplate.count(new Query(criteria), Material.class);
        PageHelper pageHelper = mongoUtil.pageHelper(count, material);
        return pageHelper;
    }

    @Override
    public List<MaterialVo> searchMaterialList()
    {
        return mongoTemplate.findAll(MaterialVo.class);
    }

    @Override
    public Boolean insertMaterial(MaterialForm materialForm)
    {
        Material material = new Material();
        BeanUtils.copyProperties(materialForm, material);
        material.setSource(Source.MANUAL.getCode());
        Material reuslt = mongoTemplate.insert(material);
        if(reuslt!=null) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean updateMaterial(MaterialForm materialForm)
    {
        Admin admin = (Admin) session.getAttribute("admin");

        Query query   = Query.query(Criteria.where("_id").is(materialForm.getId()));
        Update update = new Update();

        update.set("sku_no",        materialForm.getSkuNo());
        update.set("sku_desc",      materialForm.getSkuDesc());
        update.set("sku_spec",      materialForm.getSkuSpec());
        update.set("pallet2carton", materialForm.getPallet2carton());
        update.set("update_user",   admin.getUsername());
        update.set("update_time", 	DateUtil.date());
        UpdateResult siteResult = mongoTemplate.updateFirst(query, update, Material.class);
        if(siteResult!=null) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean deleteMaterial(String id)
    {
        Query query = Query.query(Criteria.where("_id").is(id).and("source").is(2));
        Long count = mongoTemplate.remove(query, Material.class).getDeletedCount();
        if(count>0) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean existMaterial(String skuNo)
    {
        Query query = Query.query(Criteria.where("sku_no").is(skuNo));
        return mongoTemplate.exists(query, Material.class);
    }

    @Override
    public Material getMaterialInfo(String id)
    {
        return mongoTemplate.findById(id, Material.class);
    }

    @Override
    public Material getMaterialInfoBySkuNo(String skuNo)
    {
        Query query = Query.query(Criteria.where("sku_no").is(skuNo));
        return mongoTemplate.findOne(query, Material.class);
    }
}
