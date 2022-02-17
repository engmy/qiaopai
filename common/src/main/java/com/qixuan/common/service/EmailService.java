package com.qixuan.common.service;

public interface EmailService
{
    // 普通邮件发送
    void pushEmail(String subject, String content);

    // 附件邮件发送
    void pushEmail(String subject, String content, String file);
}
