package com.qixuan.common.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.qixuan.common.enums.GroupConfig;
import com.qixuan.common.enums.ProfilesActive;
import com.qixuan.common.service.ConfigService;
import com.qixuan.common.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService
{
    @Resource
    private ConfigService configService;

    @Value("${spring.profiles.active}")
    private String profilesActive;

    @Async
    @Override
    public void pushEmail(String subject, String content)
    {
        try
        {
            String profiles = ProfilesActive.valueOf(profilesActive.toUpperCase()).getInfo();
            Map<String, String> config = configService.getConfigMapByGroupId(GroupConfig.MAILBOX.getCode());

            MailAccount mail = new MailAccount();
            mail.setAuth(true);
            mail.setSslEnable(true);
            mail.setStarttlsEnable(true);
            mail.setSocketFactoryFallback(true);
            mail.setHost(config.get("email_host"));
            mail.setFrom(config.get("email_from"));
            mail.setUser(config.get("email_user"));
            mail.setPass(config.get("email_pass"));
            mail.setPort(Integer.parseInt(config.get("email_port")));
            mail.setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
            mail.setSocketFactoryPort(Integer.parseInt(config.get("email_port")));

            MailUtil.send(mail, CollUtil.newArrayList(config.get("email_to").trim().replace("，", ",")), "【"+profiles+"】" + subject, content, true);
        } catch (Exception exception)
        {
            log.error(exception.getMessage());
        }
    }

    @Async
    @Override
    public void pushEmail(String subject, String content, String file)
    {
        try
        {
            System.setProperty("mail.mime.splitlongparameters", "false");
            String profiles = ProfilesActive.valueOf(profilesActive.toUpperCase()).getInfo();
            Map<String, String> config = configService.getConfigMapByGroupId(GroupConfig.MAILBOX.getCode());
            String report = configService.getConfigValueByKey("report_mail");

            MailAccount mail = new MailAccount();
            mail.setAuth(true);
            mail.setSslEnable(true);
            mail.setStarttlsEnable(true);
            mail.setSocketFactoryFallback(true);
            mail.setHost(config.get("email_host"));
            mail.setFrom(config.get("email_from"));
            mail.setUser(config.get("email_user"));
            mail.setPass(config.get("email_pass"));
            mail.setPort(Integer.parseInt(config.get("email_port")));
            mail.setSocketFactoryClass("javax.net.ssl.SSLSocketFactory");
            mail.setSocketFactoryPort(Integer.parseInt(config.get("email_port")));

            log.error(config.toString());
            log.error(report.trim().replace("，", ","));

            if(StrUtil.isNotEmpty(report))
            {
                MailUtil.send(mail, CollUtil.newArrayList(report.trim().replace("，", ",")), "【" + profiles + "】" + subject, content, true, FileUtil.file(file));
            } else {
                log.error("接收人不能为空");
            }
        } catch (Exception exception)
        {
            log.error("发送邮件异常:" + exception.getMessage(), exception.getCause());
        }
    }
}