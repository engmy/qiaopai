package com.qixuan.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.service.ProductService;
import com.qixuan.admin.service.RelationService;
import com.qixuan.common.entity.Product;
import com.qixuan.common.entity.Relation;
import com.qixuan.common.exception.CustomException;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class RelationServiceImpl implements RelationService
{
    @Resource
    private ProductService productService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getRelationData(Integer page, Integer limit, Map params)
    {
        Criteria criteria = new Criteria();
        // 托盘编号
        if(StrUtil.isNotEmpty((String) params.get("pallet_code")))
        {
            criteria.and("pallet_code").regex(params.get("pallet_code").toString());
        }
        // 工单号查询
        if(StrUtil.isNotEmpty((String) params.get("doc_no")))
        {
            criteria.and("doc_no").regex(params.get("doc_no").toString());
        }
        // 产品编码
        if(StrUtil.isNotEmpty((String) params.get("sku_no")))
        {
            criteria.and("sku_no").regex(params.get("sku_no").toString());
        }
        // 生产批号
        if(StrUtil.isNotEmpty((String) params.get("lot_no")))
        {
            criteria.and("lot_no").regex(params.get("lot_no").toString());
        }
        // 灌装单号
        if(StrUtil.isNotEmpty((String) params.get("filling_no")))
        {
            criteria.and("filling_no").regex(params.get("filling_no").toString());
        }
        Aggregation aggregation = Aggregation.newAggregation
        (
            Aggregation.match(criteria),
            Aggregation.group("pallet_code")
            .first("doc_no").as("doc_no")
            .first("sku_no").as("sku_no")
            .first("filling_no").as("filling_no")
            .first("sku_desc").as("sku_desc")
            .first("lot_no").as("lot_no")
            .first("line_no").as("line_no")
            .first("pline_no").as("pline_no")
            .first("team_no").as("team_no")
            .first("sequence_id").as("sequence_id")
            .first("upload_flag").as("upload_flag")
            .first("create_time").as("create_time")
            .first("mfg_date").as("mfg_date")
            .first("upload_time").as("upload_time")
            .first("pallet_code").as("pallet_code"),
            Aggregation.skip((page - 1) * page),
            Aggregation.limit(limit)
        );

        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit);

        AggregationResults<Relation> resultsList = mongoTemplate.aggregate(aggregation, "relation", Relation.class);
        List<Relation> relationList = resultsList.getMappedResults();

        // 分组记录数
        Aggregation aggregation2 = Aggregation.newAggregation(
            Aggregation.match(criteria),
            Aggregation.group("pallet_code")
        );
        AggregationResults<Relation> resultsSize = mongoTemplate.aggregate(aggregation2, "relation", Relation.class);
        Long count = new Long(resultsSize.getMappedResults().size());

        PageHelper pageHelper = mongoUtil.pageHelper(count, relationList);
        return pageHelper;
    }

    @Override
    public PageHelper getRelationList(Integer page, Integer limit, Map params)
    {
        Criteria criteria = Criteria.where("_id").ne("");
        // 工单号查询
        if(StrUtil.isNotEmpty((String) params.get("doc_no")))
        {
            criteria.and("doc_no").regex(params.get("doc_no").toString());
        }
        // 产品编码
        if(StrUtil.isNotEmpty((String) params.get("sku_no")))
        {
            criteria.and("sku_no").regex(params.get("sku_no").toString());
        }
        // 生产批号
        if(StrUtil.isNotEmpty((String) params.get("lot_no")))
        {
            criteria.and("lot_no").regex(params.get("lot_no").toString());
        }
        // 外箱码查询
        if(StrUtil.isNotEmpty((String) params.get("carton_code")))
        {
            criteria.and("carton_code").regex(params.get("carton_code").toString());
        }
        // 灌装单号
        if(StrUtil.isNotEmpty((String) params.get("filling_no")))
        {
            criteria.and("filling_no").regex(params.get("filling_no").toString());
        }
        // 托盘编号
        if(StrUtil.isNotEmpty((String) params.get("pallet_code")))
        {
            criteria.and("pallet_code").regex(params.get("pallet_code").toString());
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("mfg_time"))).with(Sort.by(Sort.Order.desc("pallet_code")));

        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<Relation> relationList = mongoTemplate.find(query, Relation.class);
        Long count = mongoTemplate.count(new Query(criteria), Relation.class);

        PageHelper pageHelper = mongoUtil.pageHelper(count, relationList);
        return pageHelper;
    }

    @Override
    public Long getCartonCodeCout(String docNo)
    {
        Query query = Query.query(Criteria.where("doc_no").is(docNo));
        return mongoTemplate.count(query, Relation.class);
    }

    @Override
    public Relation getRelationInfo(String relationId)
    {
        Query query = Query.query(Criteria.where("_id").is(relationId));
        return mongoTemplate.findOne(query, Relation.class);
    }

    @Override
    public Boolean deleteRelation(String palletCodes)
    {
        String[] palletCodeArr = palletCodes.split(",");
        for(String palletCode : palletCodeArr)
        {
            Product product = productService.getProductInfo(palletCode);
            if (product.getHttpStatus()!=0)
            {
                throw new CustomException("只有未上传状态才能删除");
            }
        }
        Boolean deleteRelation = this.deleteRelation(palletCodeArr);
        Boolean deleteProduct = productService.deleteProduct(palletCodeArr);

        if(deleteRelation && deleteProduct)
        {
            return true;
        }else{
            return false;
        }
    }

    public Boolean deleteRelation(String[] palletCodeArr)
    {
        Query query = Query.query(Criteria.where("pallet_code").in(palletCodeArr));
        Long count = mongoTemplate.remove(query, Relation.class).getDeletedCount();
        if(count>0)
        {
            return true;
        }else{
            return false;
        }
    }
}
