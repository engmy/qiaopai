package com.qixuan.api.service.impl;

import com.qixuan.api.form.RelationForm;
import com.qixuan.api.form.WorkDocForm;
import com.qixuan.api.service.ProductService;
import com.qixuan.api.service.RelationService;
import com.qixuan.common.entity.Relation;
import com.qixuan.common.service.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class RelationServiceImpl implements RelationService
{
    @Resource
    private ConfigService configService;

    @Resource
    private ProductService productService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean insertRelation(WorkDocForm workDocForm, List<RelationForm> relationFormList)
    {
        List<Relation> relationList = new ArrayList();
        String siteNo = configService.getConfigValueByKey("site_no");
        String siteName = configService.getConfigValueByKey("site_name");
        for(int i=0; i<relationFormList.size(); i++)
        {
            Relation relation = new Relation();
            RelationForm relationForm = relationFormList.get(i);
            BeanUtils.copyProperties(relationForm, relation);
            relation.setFillingNo(workDocForm.getExtend2());
            relation.setDocNo(workDocForm.getDocNo());
            relation.setSkuNo(workDocForm.getSkuNo());
            relation.setLineNo(workDocForm.getLotNo());
            relation.setSiteNo(siteNo);
            relation.setSiteDesc(siteName);
            relation.setCreateUser("plc");
            relation.setUpdateUser("plc");
            relation.setLotNo(workDocForm.getLotNo());
            relation.setSkuDesc(workDocForm.getSkuDesc());
            relation.setMfgDate(workDocForm.getMfgDate());
            relation.setDocCatalog(workDocForm.getDocCatalog());
            relationList.add(relation);
        }

        // 生产关联表写入数据
        Collection<Relation> result = mongoTemplate.insert(relationList, Relation.class);
        if(result.size()>0) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List getCartonCodeList(String docNo)
    {
        Query query = Query.query(Criteria.where("doc_no").is(docNo));
        List<Relation> relationList = mongoTemplate.find(query, Relation.class);
        List list = new ArrayList();
        if(relationList.size()==0) {
            return list;
        }
        for(int i=0; i<relationList.size(); i++)
        {
            Relation relation = relationList.get(i);
            list.add(relation.getCartonCode());
        }
        return list;
    }

    @Override
    public Boolean existCartonCode(String cartonCode)
    {
        Query query = Query.query(Criteria.where("carton_code").is(cartonCode));
        return mongoTemplate.exists(query, Relation.class);
    }

    @Override
    public Long getCartonCodeCout(String docNo)
    {
        Query query = Query.query(Criteria.where("doc_no").is(docNo));
        return mongoTemplate.count(query, Relation.class);
    }
}
