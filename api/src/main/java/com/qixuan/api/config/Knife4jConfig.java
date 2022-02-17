package com.qixuan.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig
{
    @Bean(value = "defaultApi2")
    public Docket defaultApi2()
    {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder()
            .title("齐炫生产关系系统接口文档")
            .description("齐炫生产关系系统接口文档")
            .termsOfServiceUrl("http://www.qixuan.com/")
            .contact(new Contact("胡小光", "http://www.qixuan.com", "huxiaoguang@qixuan.com"))
            .version("1.0")
            .build())
            .groupName("壳牌接口文档")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.qixuan.api.controller"))
            .paths(PathSelectors.any())
            .build();
        return docket;
    }
}
