package com.qixuan.api.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.api.form.WorkDocForm;
import com.qixuan.api.service.MaterialService;
import com.qixuan.api.service.WorkDocService;
import com.qixuan.common.entity.Material;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.enums.Source;
import com.qixuan.common.enums.WorkDocStatus;
import com.qixuan.common.exception.CustomException;
import com.qixuan.common.service.ConfigService;
import com.qixuan.common.utils.ISODateUtil;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WorkDocServiceImpl implements WorkDocService
{
    @Resource
    private ConfigService configService;

    @Resource
    private MaterialService materialService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getWorkDocList(Integer page, Integer limit, String startTime, String endTime, String siteNo, String plineNo)
    {
        Criteria criteria = Criteria.where("source").is(Source.MANUAL.getCode());
        // 工厂代码
        if(StrUtil.isNotEmpty(siteNo))
        {
            criteria.and("site_no").is(siteNo);
        }
        // 产线代码
        if(StrUtil.isNotEmpty(plineNo))
        {
            criteria.and("pline_no").is(plineNo);
        }
        if(StrUtil.isNotEmpty(startTime) && StrUtil.isEmpty(endTime))
        {
            criteria.and("create_time").gte(ISODateUtil.dateToISODate(startTime));
        }
        if(StrUtil.isNotEmpty(endTime) && StrUtil.isEmpty(startTime))
        {
            criteria.and("create_time").lt(ISODateUtil.dateToISODate(endTime));
        }
        if(StrUtil.isNotEmpty(startTime) && StrUtil.isNotEmpty(endTime))
        {
            criteria.andOperator(Criteria.where("create_time").gte(ISODateUtil.dateToISODate(startTime)), Criteria.where("create_time").lt(ISODateUtil.dateToISODate(endTime)));
        }
        Query query = Query.query(criteria);
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<WorkDoc> workDocList = mongoTemplate.find(query, WorkDoc.class);
        Long count = mongoTemplate.count(new Query(criteria), WorkDoc.class);
        PageHelper pageHelper = mongoUtil.pageHelper(count, workDocList);
        return pageHelper;
    }

    @Override
    public Boolean updateWorkDocStatus(String docNo, String docStatus)
    {
        if(!this.existWorkDoc(docNo))
        {
            throw new CustomException("工单状态不存在");
        }
        List<Object> status = EnumUtil.getFieldValues(WorkDocStatus.class, "code");
        if(!status.contains(docStatus))
        {
            throw new CustomException("工单状态不存在");
        }
        Query query = Query.query(Criteria.where("doc_no").is(docNo));
        Update update = new Update();

        update.set("doc_status",  docStatus);
        update.set("update_user", "plc");
        update.set("update_time", DateUtil.date());
		
        UpdateResult result = mongoTemplate.updateFirst(query, update, WorkDoc.class);
        if(result!=null) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean insertWorkDoc(WorkDocForm workDocForm)
    {
        WorkDoc workDoc = new WorkDoc();
        BeanUtils.copyProperties(workDocForm, workDoc);

        // 获取物料信息
        Integer pallet2carton = 39;
        Material material = materialService.getMaterialInfo(workDocForm.getSkuNo());
        if(material!=null)
        {
            pallet2carton = material.getPallet2carton();
        }

        // 获取配置
        String siteNo = configService.getConfigValueByKey("site_no");
        String siteName = configService.getConfigValueByKey("site_name");

        // 更新为已完成
        workDoc.setDocStatus(WorkDocStatus.COMPLETED.getCode());
        workDoc.setSource(Source.API.getCode());
        workDoc.setCreateUser("plc");
        workDoc.setUpdateUser("plc");
        workDoc.setSiteNo(siteNo);
        workDoc.setSiteDesc(siteName);
        workDoc.setPallet2carton(pallet2carton);

        WorkDoc reuslt = mongoTemplate.insert(workDoc);
        if(reuslt!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public WorkDoc getWorkDocInfo(String docNo, Integer type)
    {
        if(type==2)
        {
            Criteria criteria = new Criteria();
            Query query = new Query(criteria.orOperator(Criteria.where("doc_no").is(docNo), Criteria.where("extend2").is(docNo)));
            return mongoTemplate.findOne(query, WorkDoc.class);
        }else{
            Query query = Query.query(Criteria.where("doc_no").is(docNo));
            return mongoTemplate.findOne(query, WorkDoc.class);
        }
    }

    @Override
    public Long getWorkDocCount(String docNo)
    {
        Query query = Query.query(Criteria.where("doc_no").is(docNo));
        return mongoTemplate.count(query, WorkDoc.class);
    }

    @Override
    public Boolean existWorkDoc(String docNo)
    {
        Query query = Query.query(Criteria.where("doc_no").is(docNo));
        return mongoTemplate.exists(query, WorkDoc.class);
    }
}
