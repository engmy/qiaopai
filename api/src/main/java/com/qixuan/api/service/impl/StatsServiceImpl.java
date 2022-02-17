package com.qixuan.api.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.qixuan.api.service.ProductService;
import com.qixuan.api.service.RelationService;
import com.qixuan.api.service.StatsService;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.enums.WorkDocStatus;
import com.qixuan.common.utils.ISODateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatsServiceImpl implements StatsService {
    @Value("${custom.upload.report-code-file-path}")
    private String reportPath;

    @Resource
    private RelationService relationService;

    @Resource
    private ProductService productService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public String generateReport(String fileName, Date mfgDate) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(mfgDate);
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";

        Criteria criteria = new Criteria();
        Query query = Query.query(criteria.where("doc_status").is(WorkDocStatus.UPLOADED.getCode()).andOperator
                (
                        Criteria.where("mfg_date").gte(ISODateUtil.dateToISODate(startDate)),
                        Criteria.where("mfg_date").lte(ISODateUtil.dateToISODate(endDate))
                ));
        List<WorkDoc> workDocList = mongoTemplate.find(query, WorkDoc.class);

        List<Map> workDocStatsList = new ArrayList();
        if (workDocList.size() == 0) {
            return "";
        }

        for (WorkDoc workDoc : workDocList) {
            ConcurrentHashMap map = new ConcurrentHashMap();
            map.put("mfgDate", new SimpleDateFormat("yyyy-MM-dd").format(workDoc.getMfgDate()));
            map.put("plineNo", workDoc.getPlineNo());
            map.put("lotNo", workDoc.getLotNo());
            map.put("skuNo", workDoc.getSkuNo());
            map.put("extend2", workDoc.getExtend2());
            map.put("qty1", relationService.getCartonCodeCout(workDoc.getDocNo()).intValue());
            map.put("qty2", productService.getProductCartonNum(workDoc.getDocNo()));
            workDocStatsList.add(map);
        }

        String filePath = reportPath + "/" + fileName + ".xlsx";
        if (FileUtil.exist(filePath)) {
            FileUtil.del(filePath);
        }

        ExcelWriter writer = ExcelUtil.getWriter(filePath);
        writer.addHeaderAlias("mfgDate", "生产日期");
        writer.addHeaderAlias("plineNo", "产线");
        writer.addHeaderAlias("lotNo", "批号");
        writer.addHeaderAlias("skuNo", "产品编码");
        writer.addHeaderAlias("extend2", "灌装单号");
        writer.addHeaderAlias("qty1", "生产数量");
        writer.addHeaderAlias("qty2", "上传数据");

        writer.setColumnWidth(0, 20);
        writer.setColumnWidth(1, 20);
        writer.setColumnWidth(2, 20);
        writer.setColumnWidth(3, 20);
        writer.setColumnWidth(4, 20);
        writer.setColumnWidth(5, 20);
        writer.setColumnWidth(6, 20);

        writer.merge(6, fileName);
        writer.write(workDocStatsList, true);
        writer.close();

        return filePath;
    }


    @Override
    public String generateEmailText(String fileName, Date mfgDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(mfgDate);
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";

        Criteria criteria = new Criteria();
        Query query = Query.query(criteria.where("doc_status").is(WorkDocStatus.UPLOADED.getCode()).andOperator
                (
                        Criteria.where("mfg_date").gte(ISODateUtil.dateToISODate(startDate)),
                        Criteria.where("mfg_date").lte(ISODateUtil.dateToISODate(endDate))
                ));
        List<WorkDoc> workDocList = mongoTemplate.find(query, WorkDoc.class);

        if (workDocList.size() == 0) {
            return "";
        }

        String tableTemplate = tableTemplate();
        StringBuilder body = new StringBuilder();
        for (WorkDoc workDoc : workDocList){
            body.append(String.format(bodyTemplate(),
                    sdf.format(workDoc.getMfgDate()),
                    workDoc.getPlineNo(),
                    workDoc.getLotNo(),
                    workDoc.getSkuNo(),
                    workDoc.getExtend2(),
                    relationService.getCartonCodeCout(workDoc.getDocNo()).intValue(),
                    productService.getProductCartonNum(workDoc.getDocNo())));
        }
        return String.format(tableTemplate, body);
    }

    public String tableTemplate(){
        return "<table style=\"border: #D1D1D1;border-collapse: collapse;\" border=\"1\" cellspacing=\"0\" cellpadding=\"5\"><tr><th>生产日期</th><th>产线</th><th>批号</th><th>产品编码</th><th>灌装单号</th><th>生产数量</th><th>上传数据</th></tr>%s</table>";
    }

    public String bodyTemplate(){
        return "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
    }


}

