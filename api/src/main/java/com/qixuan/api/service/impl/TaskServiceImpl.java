package com.qixuan.api.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.qixuan.api.service.*;
import com.qixuan.common.entity.Product;
import com.qixuan.common.entity.Task;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.enums.UploadStatus;
import com.qixuan.common.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService
{
    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductService productService;

    @Autowired
    private YouMaService youMaService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private WorkDocService workDocService;

    private Logger logger = LoggerFactory.getLogger("product");

    @Override
    public List<Task> getTaskList()
    {
        Query query = Query.query(Criteria.where("status").is(1));
        query.with(Sort.by(Sort.Order.desc("_id")));
        return mongoTemplate.find(query, Task.class);
    }

    @Override
    public Task getTaskInfo(String taskId)
    {
        Query query = Query.query(Criteria.where("_id").is(taskId));
        return mongoTemplate.findOne(query, Task.class);
    }

    @Override
    public Boolean pushProductTask()
    {
        logger.info("pushProductTask 执行开始时间："+ DateUtil.date());

        // 查询未上传数据
        List<Product> productList = productService.getNoUploadProductList(10);
        if(CollUtil.isEmpty(productList))
        {
            return true;
        }

        logger.info("pushProductTask  正在执行时间："+ DateUtil.date());

        for(Product product : productList)
        {
            // 上传文件校验
            if(!productService.checkCartonCodeNum(product.getFile(), product.getCartonNum())) {
                continue;
            }

            // 上传腾讯优码中台
            JSONObject requireParams = new JSONObject(product);
            String jobNo = youMaService.uploadProduct(product);
            if(StrUtil.isEmpty(jobNo))
            {
                // 错误次数超过三次发送邮件通知
                Integer errorNum = product.getErrorNum();
                if(errorNum==0 || errorNum==1 || Math.floorMod(errorNum, 9)==0)
                {
                    emailService.pushEmail("生产数据上传中失败，请及时处理！", "错误次数：" + errorNum + "<br />请求参数：" + requireParams);
                }

                // 更新错误次数
                productService.updateProductErrorNum(product.getProductId(),  errorNum + 1);
                logger.info("生产数据上传腾讯优码失败，请求参数：" + requireParams);
                continue;
            }
            Boolean result = productService.updateProductJobNo(product.getProductId(), jobNo);
            if(result)
            {
                logger.info("优码工单:" + jobNo + " 上传中,待确认！请求参数：" + requireParams);
            }else{
                logger.info("优码工单:" + jobNo + " 上传失败！请求参数：" + requireParams);
            }
        }
        logger.info("pushProductTask 执行结束时间："+ DateUtil.date());
        return true;
    }

    @Override
    public Boolean pushRelationTask()
    {
        logger.info("pushRelationTask 执行开始时间："+ DateUtil.date());

        // 查询已上传未确认数据
        List<Product> productList = productService.getNoConfirmProductList();
        if(CollUtil.isEmpty(productList)) {
            return true;
        }

        logger.info("pushRelationTask 正在执行时间："+ DateUtil.date());

        for(Product product : productList)
        {
            JSONObject requireParams = new JSONObject(product);
            Map<String, Object> confirm = productService.confirmProduct(product.getJobNo(), product.getCompanyId());
            if(!confirm.get("code").equals("SUCCESS"))
            {
                // 错误次数超过三次发送邮件通知
                Integer errorNum = product.getErrorNum();
                if(errorNum==0 || errorNum==1 || Math.floorMod(errorNum, 9)==0)
                {
                    emailService.pushEmail("生产数据确认上传失败，请及时处理！", "错误次数：" + errorNum + "<br />请求参数：" + requireParams + "<br />响应参数：" + confirm.get("msg"));
                }
                // 更新错误次数
                if(confirm.get("code").equals("EXCEPTION"))
                {
                    productService.updateProductStatus(product.getProductId(), UploadStatus.EXCEPTION.getCode());
                    logger.error("优码工单:" + product.getJobNo() + " 确认异常！请求参数：" + requireParams + "<br />响应参数：" + confirm.get("msg"));
                }else{
                    logger.error("优码工单:" + product.getJobNo() + " 确认失败！请求参数：" + requireParams + "<br />响应参数：" + confirm.get("msg"));
                }
                productService.updateProductErrorNum(product.getProductId(),  errorNum + 1);
                continue;
            }
            Boolean result = productService.updateProductStatus(product.getProductId(), UploadStatus.Uploaded.getCode());
            if(result)
            {
                logger.info("优码工单:" + product.getJobNo() + " 更新状态为已上传！请求参数：" + requireParams);
            }else{
                logger.info("优码工单:" + product.getJobNo() + " 更新状态为已失败！请求参数：" + requireParams);
            }
        }

        // 生成工单数据报表
        WorkDoc workdoc = workDocService.getWorkDocInfo(productList.get(0).getDocNo(), 1);
        String mfgDate  = new SimpleDateFormat("yyyyMMdd").format(workdoc.getMfgDate());
        String fileName = "生产数据报表" + mfgDate;
        String reportFile = statsService.generateReport(fileName, workdoc.getMfgDate());
        String emailText = statsService.generateEmailText(fileName, workdoc.getMfgDate());
        if(StrUtil.isNotEmpty(reportFile))
        {
            emailService.pushEmail(fileName, "生产数据上传完成，请注意查收.\n\n\n" + emailText, reportFile);
        }
        logger.info("pushRelationTask 执行结束时间："+ DateUtil.date());
        return true;
    }
}