package com.qixuan.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@EnableMongoAuditing
@SpringBootApplication(scanBasePackages = "com.qixuan.*")
public class ApiApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ApiApplication.class, args);
        System.out.println("壳牌生产接口系统启动成功");
    }
}
