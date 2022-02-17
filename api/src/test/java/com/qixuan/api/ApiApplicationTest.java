package com.qixuan.api;

import com.qixuan.api.service.TaskService;
import com.qixuan.common.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
class ApiApplicationTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskService taskService;
    @Test
    public void test(){
        emailService.pushEmail("123", "<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\"><tr><th>生产日期</th><th>产线</th><th>批号</th><th>产品编码</th><th>灌装单号</th><th>生产数量</th><th>上传数据</th></tr><tr><td>Wed Sep 08 00:00:00 CST 2021</td><td>A3320</td><td>2209000</td><td>550045847</td><td>112123421</td><td>4</td><td>4</td></tr></table>", "C:\\Users\\EDY\\Desktop\\code_generation.fxml");
    }


    @Test
    public void test2(){
        taskService. pushRelationTask();
    }


}