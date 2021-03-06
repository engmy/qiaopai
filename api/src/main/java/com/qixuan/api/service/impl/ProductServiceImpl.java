package com.qixuan.api.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.api.form.RelationForm;
import com.qixuan.api.form.WorkDocForm;
import com.qixuan.api.service.ProductService;
import com.qixuan.api.service.YouMaService;
import com.qixuan.common.entity.Product;
import com.qixuan.common.entity.Relation;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.enums.GroupConfig;
import com.qixuan.common.enums.UploadStatus;
import com.qixuan.common.enums.WorkDocStatus;
import com.qixuan.common.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService
{
    @Value("${custom.upload.pallet-code-file-path}")
    private String palletCodFilePath;

    @Resource
    private YouMaService youMaService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private ConfigService configService;

    @Override
    public Boolean insertProduct(WorkDocForm workDocForm, List<RelationForm> relationFormList)
    {
        List<Product> productList = new ArrayList();
        List<RelationForm> palletRelationFormList = this.getPalletRelationFormList(relationFormList);

        Map<String, String> company = configService.getConfigMapByGroupId(GroupConfig.COMPANY.getCode());
        Map<String, String> factory = configService.getConfigMapByGroupId(GroupConfig.FACTORY.getCode());

        for(int i=0; i < palletRelationFormList.size(); i++)
        {
            Product product = new Product();
            RelationForm relation = palletRelationFormList.get(i);

            // ???????????????
            String[] codes = relation.getCartonCode().split(",");
            Long cartonNum = Arrays.stream(codes).count();

            // ??????????????????
            String filePath = this.getPalletCodeFile(relation.getPalletCode(), relation.getCartonCode());

            // ??????????????????
            JSONObject dataJson = JSONUtil.createObj();
            dataJson.set("productOrder", "");
            dataJson.set("productCode", workDocForm.getSkuNo());
            dataJson.set("productFactoryCode", factory.get("site_no"));
            dataJson.set("productFactoryName", factory.get("site_name"));
            dataJson.set("productLine", relation.getPlineNo());
            dataJson.set("productBatch", relation.getTeamNo());
            dataJson.set("vcode", relation.getPalletCode());
            dataJson.set("virtualCode", relation.getExtend1());
            dataJson.set("scratchCode", relation.getBoxCode());
            dataJson.set("productionTime", DateUtil.formatDateTime(relation.getMfgTime()));

            product.setFile(filePath);
            product.setCreateUser("plc");
            product.setUpdateUser("plc");
            product.setCartonDigit(codes[0].length());
            product.setFillingNo(workDocForm.getExtend2());
            product.setCartonNum(cartonNum.intValue());
            product.setVirtualCode(relation.getExtend1());
            product.setPalletCode(relation.getPalletCode());
            product.setDocNo(workDocForm.getDocNo());
            product.setDataJson(dataJson.toString());
            product.setCompanyId(company.get("company_no"));
            productList.add(product);
        }
        Collection<Product> result = mongoTemplate.insert(productList, Product.class);
        if(result.size()>0) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<Product> getNoUploadProductList()
    {
        Query query = Query.query(Criteria.where("http_status").is(0).and("file").ne(""));
        List<Product> productList = mongoTemplate.find(query, Product.class);
        return productList;
    }

    @Override
    public List<Product> getNoUploadProductList(Integer num)
    {
        Query query = Query.query(Criteria.where("http_status").is(0).and("file").ne("").and("carton_digit").is(num));
        List<Product> productList = mongoTemplate.find(query, Product.class);
        return productList;
    }

    @Override
    public List<Product> getNoConfirmProductList()
    {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("http_status").is(100), Criteria.where("http_status").is(500)).and("job_no").ne("");
        Query query = new Query(criteria);
        List<Product> productList = mongoTemplate.find(query, Product.class);
        return productList;
    }

    @Override
    public Map<String, Object> confirmProduct(String jobNo, String companyId)
    {
        return youMaService.existProduct(jobNo, companyId);
    }

    @Override
    public Boolean updateProductStatus(String productId, Integer status)
    {
        DateTime confirmTime = DateUtil.date();
        Query productQuery = Query.query(Criteria.where("_id").is(productId));
        Product product = mongoTemplate.findOne(productQuery, Product.class);

        // ?????????????????????(????????????)
        Update productUpdate = new Update();
        productUpdate.set("http_status",  status);      // ???????????????
        productUpdate.set("confirm_time", confirmTime); // ????????????
        UpdateResult productResult = mongoTemplate.updateMulti(productQuery, productUpdate, Product.class);

        // ?????????????????????(?????????)
        Query relationQuery = Query.query(Criteria.where("pallet_code").is(product.getPalletCode()));
        Update relationUpdate = new Update();
        relationUpdate.set("upload_flag",  status);     // ???????????????
        relationUpdate.set("confirm_time", confirmTime); // ????????????
        UpdateResult relationResult = mongoTemplate.updateMulti(relationQuery, relationUpdate, Relation.class);

        // ????????????????????????
        String workdocStatus = WorkDocStatus.COMPLETED.getCode();

        // ???????????????????????????????????????
        if(this.checkPalletCodeNum(product.getDocNo()))
        {
            workdocStatus = WorkDocStatus.UPLOADED.getCode();
        }else{
            workdocStatus = WorkDocStatus.EXCEPTION.getCode();
        }

        Query workDocQuery = Query.query(Criteria.where("doc_no").is(product.getDocNo()));
        Update workDocuUdate = new Update();
        workDocuUdate.set("doc_status", workdocStatus);
        workDocuUdate.set("update_user", "plc");
		workDocuUdate.set("update_time",  DateUtil.date());
        UpdateResult workdocResult = mongoTemplate.updateFirst(workDocQuery, workDocuUdate, WorkDoc.class);

        if(productResult!=null && relationResult!=null && workdocResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean updateProductJobNo(String productId, String jobNo)
    {
        DateTime uploadTime = DateUtil.date();

        Query productQuery = Query.query(Criteria.where("_id").is(productId));
        Product product = mongoTemplate.findOne(productQuery, Product.class);

        // ?????????????????????
        Update productUpdate = new Update();
        productUpdate.set("job_no", jobNo);
        productUpdate.set("http_status", UploadStatus.UPLOADING.getCode()); // ?????????????????????
        productUpdate.set("upload_time", uploadTime); // ????????????
        UpdateResult productResult = mongoTemplate.updateMulti(productQuery, productUpdate, Product.class);

        // ?????????????????????
        Query  relationQuery  = Query.query(Criteria.where("pallet_code").is(product.getPalletCode()));
        Update relationUpdate = new Update();
        relationUpdate.set("upload_guid", productId);
        relationUpdate.set("upload_flag", UploadStatus.UPLOADING.getCode()); // ?????????????????????
        relationUpdate.set("upload_time", uploadTime); // ????????????
        UpdateResult relationResult = mongoTemplate.updateMulti(relationQuery, relationUpdate, Relation.class);

        if(productResult!=null && relationResult!=null)
        {
            return true;
        }else{
            return false;
        }
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
        for (Product product : productList)
        {
            cartonNum += product.getCartonNum();
        }
        return cartonNum;
    }

    @Override
    public Boolean updateProductErrorNum(String productId, Integer num)
    {
        Query query = Query.query(Criteria.where("_id").is(productId));
        Update update = new Update();
        update.set("error_num", num);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Product.class);
        if(updateResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean updateProductFilePath(String productId, String filePath)
    {
        Query query = Query.query(Criteria.where("_id").is(productId));
        Update update = new Update();
        update.set("file", filePath);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Product.class);
        if(updateResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean checkCartonCodeNum(String filePath, Integer cartonNum)
    {
        File file = new File(filePath);
        if (!file.exists() && !file .isDirectory())
        {
            log.error(filePath + "?????????");
            return false;
        }

        FileReader fileReader = new FileReader(filePath);
        Integer lineNum = fileReader.readLines().size();

        if(lineNum==0)
        {
            log.error(filePath + "????????????????????????");
            return false;
        }

        if(cartonNum == (lineNum-1))
        {
            return true;
        }else{
            log.error(filePath + "????????????????????????");
            return false;
        }
    }

    @Override
    public Product getProductInfoByJobNo(String jobNo)
    {
        Query query = Query.query(Criteria.where("job_no").is(jobNo));
        return mongoTemplate.findOne(query, Product.class);
    }

    /**
     * ??????????????????
     */
    public String getPalletCodeFile(String palletCode, String cartonCode)
    {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        String dir = DateUtil.format(new Date(), "yyyyMMdd");
        String filePath = this.palletCodFilePath + "/" + dir + "/" + snowflake.nextId() + ".txt";

        FileWriter writer = new FileWriter(filePath);
        writer.append("PG3-1:" + palletCode + "\r\n");

        String[] codes = cartonCode.split(",");
        for (String code : codes)
        {
            writer.append(code.trim() + "\r\n");
        }
        return filePath;
    }

    /**
     * ??????????????????
     * @param relationFormList
     * @return
     */
    public List<RelationForm> getPalletRelationFormList(List<RelationForm> relationFormList)
    {
        List<RelationForm> newRelationFormList = new ArrayList();

        // ???????????????
        Map<String, List<RelationForm>> relationMap = relationFormList.stream().collect(Collectors.groupingBy(RelationForm::getPalletCode));
        for (Object key : relationMap.keySet())
        {
            // ???????????????????????????
            List<RelationForm> relationList = relationMap.get(key);
            Map<String, Set<String>> codeMap = relationList.stream().collect(Collectors.groupingBy(RelationForm::getPalletCode, Collectors.mapping(RelationForm::getCartonCode, Collectors.toSet())));

            List<RelationForm> newList = new ArrayList();

            for(int i=0; i < relationList.size(); i++)
            {
                String cartonCode = String.join(",", codeMap.get(key));
                RelationForm produceForm = relationList.get(i);

                RelationForm newProduceForm = new RelationForm();
                BeanUtils.copyProperties(produceForm, newProduceForm);
                newProduceForm.setCartonCode(cartonCode);
                newList.add(newProduceForm);
            }

            // ???????????????
            newList.stream().distinct().collect(Collectors.toList());
            newRelationFormList.add(newList.get(0));
        }
        return newRelationFormList;
    }

    @Override
    public Integer getPalletCodeNum(String docNo)
    {
        Query query = Query.query(Criteria.where("doc_no").is(docNo));
        Long palletCodeNum = mongoTemplate.count(query, Product.class);
        return palletCodeNum.intValue();
    }

    @Override
    public Integer getUploadPalletCodeNum(String docNo)
    {
        Query query = Query.query(Criteria.where("http_status").is(200).and("doc_no").is(docNo));
        Long palletCodeNum = mongoTemplate.count(query, Product.class);
        return palletCodeNum.intValue();
    }

    @Override
    public Boolean checkPalletCodeNum(String docNo)
    {
        Integer uploadNum = this.getUploadPalletCodeNum(docNo);
        Integer palletNum = this.getPalletCodeNum(docNo);

        if(uploadNum.equals(palletNum))
        {
            return true;
        }else{
            return false;
        }
    }
}