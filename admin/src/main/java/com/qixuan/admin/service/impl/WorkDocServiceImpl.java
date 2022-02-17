package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.admin.form.WorkDocForm;
import com.qixuan.admin.service.ConfigService;
import com.qixuan.admin.service.ProductService;
import com.qixuan.admin.service.RelationService;
import com.qixuan.admin.service.WorkDocService;
import com.qixuan.admin.vo.WorkDocVo;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.enums.Source;
import com.qixuan.common.exception.CustomException;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkDocServiceImpl implements WorkDocService
{
    @Resource
    private HttpSession session;

    @Resource
    private ConfigService configService;

    @Resource
    private RelationService relationService;

    @Resource
    private ProductService productService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getWorkDocList(Integer page, Integer limit, Map params)
    {
        Criteria criteria = Criteria.where("_id").ne("");
        // 工单查询
        if(StrUtil.isNotEmpty((String) params.get("doc_no")))
        {
            criteria.and("doc_no").regex(params.get("doc_no").toString());
        }
        // 产品查询
        if(StrUtil.isNotEmpty((String) params.get("sku_no")))
        {
            criteria.and("sku_no").regex(params.get("sku_no").toString());
        }
        // 批号查询
        if(StrUtil.isNotEmpty((String) params.get("lot_no")))
        {
            criteria.and("lot_no").regex(params.get("lot_no").toString());
        }
        // 产线查询
        if(StrUtil.isNotEmpty((String) params.get("pline_no")))
        {
            criteria.and("pline_no").regex(params.get("pline_no").toString());
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<WorkDoc> workDocList = mongoTemplate.find(query, WorkDoc.class);

        List<WorkDocVo> workDocStatsList = new ArrayList();
        if(workDocList.size()>0)
        {
            for (int i = 0; i < workDocList.size(); i++)
            {
                WorkDoc workDoc = workDocList.get(i);
                String docNo = workDoc.getDocNo();
                WorkDocVo workDocStats = new WorkDocVo();
                BeanUtils.copyProperties(workDoc, workDocStats);
                workDocStats.setQty1(relationService.getCartonCodeCout(docNo).intValue());
                workDocStats.setQty2(productService.getProductCartonNum(docNo));
                workDocStatsList.add(workDocStats);
            }
        }
        Long count = mongoTemplate.count(new Query(criteria), WorkDoc.class);
        PageHelper pageHelper = mongoUtil.pageHelper(count, workDocStatsList);
        return pageHelper;
    }

    @Override
    public List<WorkDocVo> getWorkDocList(Map params)
    {
        Criteria criteria = Criteria.where("_id").ne("");
        // 工单查询
        if(StrUtil.isNotEmpty((String) params.get("doc_no")))
        {
            criteria.and("doc_no").regex(params.get("doc_no").toString());
        }
        // 产品查询
        if(StrUtil.isNotEmpty((String) params.get("sku_no")))
        {
            criteria.and("sku_no").regex(params.get("sku_no").toString());
        }
        // 批号查询
        if(StrUtil.isNotEmpty((String) params.get("lot_no")))
        {
            criteria.and("lot_no").regex(params.get("lot_no").toString());
        }
        // 产线查询
        if(StrUtil.isNotEmpty((String) params.get("pline_no")))
        {
            criteria.and("pline_no").regex(params.get("pline_no").toString());
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        List<WorkDoc> workDocList = mongoTemplate.find(query, WorkDoc.class);

        List<WorkDocVo> workDocStatsList = new ArrayList();
        if(workDocList.size()>0)
        {
            for (int i = 0; i < workDocList.size(); i++)
            {
                WorkDoc workDoc = workDocList.get(i);
                String docNo = workDoc.getDocNo();
                WorkDocVo workDocStats = new WorkDocVo();
                BeanUtils.copyProperties(workDoc, workDocStats);
                workDocStats.setQty1(relationService.getCartonCodeCout(docNo).intValue());
                workDocStats.setQty2(productService.getProductCartonNum(docNo));
                workDocStatsList.add(workDocStats);
            }
        }

        return workDocStatsList;
    }

    @Override
    public Boolean insertWorkDoc(WorkDocForm workDocForm)
    {
        Admin admin = (Admin) session.getAttribute("admin");

        WorkDoc workDoc = new WorkDoc();
        BeanUtils.copyProperties(workDocForm, workDoc);

        if(workDoc.getLotNo().length()!=8)
        {
            throw new CustomException("产品批号长度为8位！");
        }
        if(workDoc.getExtend2().length()!=7)
        {
            throw new CustomException("灌装单号长度为7位！");
        }

        // 获取配置
        String siteNo = configService.getConfigByKey("site_no");
        String siteName = configService.getConfigByKey("site_name");

        workDoc.setSource(Source.MANUAL.getCode());
        workDoc.setDocNo(this.createDocNo(workDocForm));
        workDoc.setCreateUser(admin.getUsername());
        workDoc.setUpdateUser(admin.getUsername());
        workDoc.setSiteNo(siteNo);
        workDoc.setSiteDesc(siteName);

        WorkDoc reuslt = mongoTemplate.insert(workDoc);
        if(reuslt!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean updateWorkDoc(WorkDocForm workDocForm)
    {
        WorkDoc workDoc = mongoTemplate.findById(workDocForm.getId(), WorkDoc.class);
        if(!workDoc.getDocStatus().equals("00"))
        {
            throw new CustomException("只有待执行工单才能修改");
        }
        if(!workDoc.getSource().equals(Source.MANUAL.getCode()))
        {
            throw new CustomException("只有手工单才能修改");
        }
        Admin admin = (Admin) session.getAttribute("admin");
        Query query = Query.query(Criteria.where("_id").is(workDocForm.getId()));
        Update update = new Update();
        update.set("sku_no",        workDocForm.getSkuNo());
        update.set("mfg_date",      workDocForm.getMfgDate());
        update.set("sku_desc",      workDocForm.getSkuDesc());
        update.set("lot_no",        workDocForm.getLotNo());
        update.set("doc_status",    workDocForm.getDocStatus());
        update.set("req_qty",       workDocForm.getReqQty());
        update.set("update_user",   admin.getUsername());
		update.set("update_time", 	DateUtil.date());
        update.set("extend2",       workDocForm.getExtend2());

        UpdateResult siteResult = mongoTemplate.updateFirst(query, update, WorkDoc.class);
        if(siteResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean deleteWorkDoc(String workDocId)
    {
        WorkDoc workDoc = mongoTemplate.findById(workDocId, WorkDoc.class);
        if(!workDoc.getDocStatus().equals("00"))
        {
            throw new CustomException("只有待执行工单才能删除");
        }
        if(!workDoc.getSource().equals(Source.MANUAL.getCode()))
        {
            throw new CustomException("只有手工单才能删除");
        }
        Query query = Query.query(Criteria.where("_id").is(workDocId).and("source").is(2).and("doc_status").is("00"));
        Long count = mongoTemplate.remove(query, WorkDoc.class).getDeletedCount();
        if(count>0)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean existWorkDoc(String skuNo)
    {
        Query query = Query.query(Criteria.where("sku_no").is(skuNo));
        return mongoTemplate.exists(query, WorkDoc.class);
    }

    @Override
    public WorkDoc getWorkDocInfo(String id)
    {
        return mongoTemplate.findById(id, WorkDoc.class);
    }

    /**
     * 生成工单号
     * @param workDocForm
     * @return
     */
    public String createDocNo(WorkDocForm workDocForm)
    {
        String nowtime = new SimpleDateFormat("yyyyMMdd").format(new Date());

        Criteria criteria = new Criteria("doc_no");
        criteria.regex(nowtime).and("source").is(2);
        Query query = new Query(criteria);
        List<WorkDoc> workDocList = mongoTemplate.find(query, WorkDoc.class);

        String serialNo = "";
        if(!workDocList.isEmpty())
        {
            List docNoList = workDocList.stream().map(WorkDoc::getDocNo).collect(Collectors.toList());
            String[] numbers = Collections.max(docNoList).toString().split("#");
            String maxNo = numbers[numbers.length - 1];
            DecimalFormat decimalFormat = new DecimalFormat("0000");
            serialNo = decimalFormat.format(Integer.parseInt(maxNo) + 1);
        }else{
            serialNo = "0001";
        }
        String siteNo = configService.getConfigByKey("site_no");
        return "GDF#" + workDocForm.getSkuNo() +"#"+workDocForm.getLotNo() +"#"+ siteNo +"#"+ workDocForm.getPlineNo() +"#"+ nowtime +"#"+ serialNo;
    }
}
