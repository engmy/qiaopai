package com.qixuan.api.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.qixuan.api.form.RelationForm;
import com.qixuan.api.form.WorkDocForm;
import com.qixuan.api.form.WorkDocRelationForm;
import com.qixuan.api.service.*;
import com.qixuan.api.vo.WorkDocDetailVo;
import com.qixuan.api.vo.WorkDocStatsVo;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.enums.WorkDocStatus;
import com.qixuan.common.exception.CustomException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkDocRelationServiceImpl implements WorkDocRelationService
{
    @Resource
    private WorkDocService workDocService;

    @Resource
    private RelationService relationService;

    @Resource
    private ProductService productService;

    @Resource
    private MaterialService materialService;

    @Resource
    private ProductLineService productLineService;

    @Override
    @Transactional
    public Boolean uploadWorkDocRelation(WorkDocRelationForm workDocRelationForm)
    {
        Boolean workDocResult = true;
        WorkDocForm workDocForm = new WorkDocForm();
        BeanUtils.copyProperties(workDocRelationForm, workDocForm);

        // 判断产线代码是否存在
        if(!productLineService.existProductLine(workDocForm.getPlineNo()))
        {
            throw new CustomException("产线代码不存在！");
        }

        // 判断产品编码是否存在
        /*
        if(!materialService.existMaterial(workDocForm.getSkuNo()))
        {
            throw new CustomException("产品编码不存在！");
        }*/

        List cartonCodeTen = new ArrayList();
        List cartonCodeTwelve = new ArrayList();

        // 判断桶码是否存在
        List<RelationForm> relationFormList = workDocRelationForm.getData();
        for(int i=0; i<relationFormList.size(); i++)
        {
            RelationForm relationForm = relationFormList.get(i);
            if(relationService.existCartonCode(relationForm.getCartonCode()))
            {
                throw new CustomException("外箱码已存在！");
            }
            if(relationForm.getCartonCode().length()!=10 && relationForm.getCartonCode().length()!=12)
            {
                throw new CustomException("外箱码长度错误！");
            }
            if(relationForm.getCartonCode().length()==10)
            {
                cartonCodeTen.add(relationForm.getCartonCode());
            }else {
                cartonCodeTwelve.add(relationForm.getCartonCode());
            }
        }

        if(ObjectUtil.isNotEmpty(cartonCodeTen) && ObjectUtil.isNotEmpty(cartonCodeTwelve))
        {
            throw new CustomException("外箱码长度不一致！");
        }

        // 写入工单表
        if(!workDocService.existWorkDoc(workDocRelationForm.getDocNo()))
        {
            workDocResult = workDocService.insertWorkDoc(workDocForm);
        }else{
            // 更新工单状态为已完成
            workDocResult = workDocService.updateWorkDocStatus(workDocRelationForm.getDocNo(), WorkDocStatus.COMPLETED.getCode());
        }

        // 写入生产管理表
        Boolean relationResult = relationService.insertRelation(workDocForm, relationFormList);

        // 写入数据到生产关联表
        Boolean productResult = productService.insertProduct(workDocForm, relationFormList);

        // 优码产品表
        if(workDocResult && relationResult && productResult)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public WorkDocStatsVo getWorkDocStats(String docNo)
    {
        WorkDoc workDoc = workDocService.getWorkDocInfo(docNo, 1);
        if(workDoc==null)
        {
            throw new CustomException("工单号不存在！");
        }
        WorkDocStatsVo workDocStats = new WorkDocStatsVo();
        BeanUtils.copyProperties(workDoc, workDocStats);
        workDocStats.setQty1(relationService.getCartonCodeCout(docNo).intValue());
        workDocStats.setQty2(productService.getProductCartonNum(docNo));
        return workDocStats;
    }

    @Override
    public WorkDocDetailVo getWorkDocDetail(String docNo)
    {
        WorkDoc workDoc = workDocService.getWorkDocInfo(docNo, 1);
        if(workDoc==null)
        {
            throw new CustomException("工单号不存在！");
        }
        WorkDocDetailVo workDocDetail = new WorkDocDetailVo();
        BeanUtils.copyProperties(workDoc, workDocDetail);
        workDocDetail.setQty1(relationService.getCartonCodeCout(docNo).intValue());
        workDocDetail.setCartonCode(relationService.getCartonCodeList(docNo));
        return workDocDetail;
    }
}
