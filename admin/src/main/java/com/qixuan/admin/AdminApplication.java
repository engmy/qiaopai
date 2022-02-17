package com.qixuan.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.qixuan.*")
public class AdminApplication
{
    public static void main(String[] args) throws JsonProcessingException
    {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("壳牌生产管理系统启动成功");
    }
}
