package com.qixuan.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qixuan.admin.service.ConfigService;
import com.qixuan.admin.service.YouMaService;
import com.qixuan.common.enums.GroupConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class YouMaServiceImpl implements YouMaService
{
    private String baseUrl;

    private String account;

    private String password;

    @Resource
    private ConfigService configService;

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
        try{
            String url = this.baseUrl + "/data/isv/login/v1";
            JSONObject param = JSONUtil.createObj();
            param.set("account",  this.account);
            param.set("password", this.password);
            String jsonString = param.toString();

            String result = HttpRequest.post(url).body(jsonString).execute().body();

            // 返回XML处理
            if(!JSONUtil.isJsonObj(result)) {
                log.error(result);
                return token;
            }

            JSONObject jsonObject = new JSONObject(result);
            token = jsonObject.getStr("accessToken");
            String ecode = jsonObject.getStr("ecode");
            if (StrUtil.isNotEmpty(ecode)) {
                log.error(jsonObject.toString());
            }
            return token;
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            return token;
        }
    }

    @Override
    public String viewProduct(String jobNo, String companyId)
    {
        String url = this.baseUrl + "/dm/isv/links/v1/logs/jobno?jobNo=" + jobNo + "&companyId=" + companyId;
        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", "Bearer " + this.getToken())
                .timeout(20000)
                .execute();

        String result = response.body();
        if(JSONUtil.isJsonObj(result))
        {
            JSONObject jsonObject = new JSONObject(result);
            String ecode = jsonObject.getStr("ecode");
            if (StrUtil.isNotEmpty(ecode) && ecode.equals("10001"))
            {
                log.error(result);
                this.getToken();
            }
        }
        if(JSONUtil.isJsonArray(result))
        {
            return result;
        }
        return result;
    }
}