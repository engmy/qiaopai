package com.qixuan.api.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qixuan.api.service.ProductLogService;
import com.qixuan.api.service.ProductService;
import com.qixuan.api.service.YouMaService;
import com.qixuan.common.entity.Product;
import com.qixuan.common.enums.GroupConfig;
import com.qixuan.common.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class YouMaServiceImpl implements YouMaService
{
    private String baseUrl;

    private String account;

    private String password;

    @Resource
    private ConfigService configService;

    @Resource
    private ProductService productService;

    @Resource
    private ProductLogService productlogService;

    private Logger logger = LoggerFactory.getLogger("product");

    @Autowired
    private void getYouMaConfig()
    {
        Map<String, String> youMaConfig = configService.getConfigMapByGroupId(GroupConfig.YOUMA.getCode());
        this.baseUrl = youMaConfig.get("base-url");
        this.account = youMaConfig.get("account");
        this.password = youMaConfig.get("password");
    }

    @Override
    @Cacheable("token")
    public String getToken()
    {
        String token = "";
        try {

            String url = this.baseUrl + "/data/isv/login/v1";
            JSONObject param = JSONUtil.createObj();
            param.set("account",  this.account);
            param.set("password", this.password);
            String jsonString = param.toString();

            String result = HttpRequest.post(url).body(jsonString).execute().body();

            // 返回XML处理
            if(!JSONUtil.isJsonObj(result))
            {
                logger.error(result);
                return token;
            }

            JSONObject jsonObject = new JSONObject(result);
            token = jsonObject.getStr("accessToken");
            String ecode = jsonObject.getStr("ecode");
            if (StrUtil.isNotEmpty(ecode))
            {
                logger.error(jsonObject.toString());
            }
            return token;
        }catch (Exception exception)
        {
            logger.error(exception.getMessage());
            return token;
        }
    }

    @Override
    public String uploadProduct(Product product)
    {
        String jobNo = "";
        String token = this.getToken();
        if(StrUtil.isEmpty(token))
        {
            return jobNo;
        }

        try {

            product.setUploadTime(new Date());
            String companyId = product.getCompanyId();
            String url = this.baseUrl + "/tm/isv/links/v1/logs1/file?companyId="+companyId;

            HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + token)
                .form("companyId", companyId)
                .form("linkCode", product.getLinkCode())
                .form("fileCodeType", product.getFileCodeType())
                .form("codeType", product.getCodeType())
                .form("dataJson", product.getDataJson())
                .form("file", new File(product.getFile()))
                .timeout(20000)
                .execute();
            String result = response.body();

            // 记录日志
            Map request = JSONUtil.parseObj(product);
            request.put("title",  "上传生产数据到优码");
            request.put("url",    url);
            request.put("ip",     this.baseUrl);
            request.put("action", "pushProductTask");
            request.put("method", "POST");
            productlogService.insertProductLog(JSONUtil.parseObj(request), response);

            // 返回XML处理
            if(!JSONUtil.isJson(result))
            {
                logger.error(result);
                return jobNo;
            }

            JSONObject jsonObject = JSONUtil.parseObj(result);
            if(200!=response.getStatus())
            {
                logger.error(result);
                if(jsonObject.getStr("ecode").equals("10001"))
                {
                    this.getToken();
                }
            }
            return jsonObject.getStr("msg");
        }catch (Exception exception)
        {
            logger.error(exception.getMessage());
        }
        return jobNo;
    }

    @Override
    public Map<String, Object> existProduct(String jobNo, String companyId)
    {
        Map map = new HashMap();

        String token = this.getToken();
        if(StrUtil.isEmpty(token))
        {
            map.put("code", "FAIL");
            map.put("msg",  "Token不能为空");
            return map;
        }

        try {

            Product product = productService.getProductInfoByJobNo(jobNo);
            if(product==null)
            {
                map.put("code", "FAIL");
                map.put("msg",  "优码工单错误");
                return map;
            }

            product.setUploadTime(new Date());
            String url = this.baseUrl + "/dm/isv/links/v1/logs/jobno?jobNo=" + jobNo + "&companyId=" + companyId;
            HttpResponse response = HttpRequest.get(url)
            .header("Authorization", "Bearer " + token)
            .timeout(20000)
            .execute();
            String result = response.body();

            // 记录日志
            Map request = JSONUtil.parseObj(product);
            request.put("title", "确认生产数据到优码");
            request.put("url",    url);
            request.put("ip",     this.baseUrl);
            request.put("action", "pushRelationTask");
            request.put("method", "GET");
            productlogService.insertProductLog(JSONUtil.parseObj(request), response);

            // 返回XML处理
            if(!JSONUtil.isJson(result))
            {
                map.put("code", "FAIL");
                map.put("msg",  "解析错误");
                return map;
            }

            if(200!=response.getStatus())
            {
                logger.error(result);
                JSONObject jsonObject = JSONUtil.parseObj(result);
                if(jsonObject.getStr("ecode").equals("10001"))
                {
                    this.getToken();
                }
                map.put("code", "FAIL");
                map.put("msg",  jsonObject);
                return map;
            }

            if(2==ObjectUtil.length(result))
            {
                map.put("code", "FAIL");
                map.put("msg",  "未查询到数据");
                return map;
            }

            JSONArray jsonArray = JSONUtil.parseArray(result);
            JSONObject jsonObj = (JSONObject) jsonArray.get(0);

            if(jsonObj.getStr("fileStatus").equals("SUCCESS"))
            {
                map.put("code", "SUCCESS");
                map.put("msg",  jsonObj.getStr("fileStatus"));
            }else{
                map.put("code", jsonObj.getStr("fileStatus"));
                map.put("msg",  jsonObj);
            }
            return map;
        }catch (Exception exception)
        {
            logger.error(exception.getMessage());
        }

        map.put("code", "FAIL");
        map.put("msg",  "Token不能为空");
        return map;
    }
}