package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.admin.service.AdminService;
import com.qixuan.admin.service.AuthService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.Material;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService
{
    @Resource
    private AdminService adminService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public Admin userLogin(String username, String password)
    {
        Admin admin = adminService.getUserByUserName(username);
        if(admin == null)
        {
            return null;
        }
        String enpassword = SecureUtil.md5(password + admin.getSalt());
        Criteria criteria = Criteria.where("username").is(username).and("password").is(enpassword).and("is_delete").is(0).and("status").is(1);
        Query query = Query.query(criteria);

        Admin admin1 = mongoTemplate.findOne(query, Admin.class);

        Query queryUpdate   = Query.query(Criteria.where("username").is(username).and("is_delete").is(0).and("status").is(1));
        Update update = new Update();
        if(admin1 != null){
            update.set("err_count", 0);
        }else {
            update.set("err_count", admin.getErrCount() + 1);
        }
        update.set("login_date",DateUtil.date());
        mongoTemplate.updateFirst(queryUpdate, update, Admin.class);

        return admin1;
    }

    @Override
    public String isErrLogion(String username)
    {
        Criteria criteria = Criteria.where("username").is(username).and("is_delete").is(0).and("status").is(1);
        Query query = Query.query(criteria);

        Admin admin = mongoTemplate.findOne(query, Admin.class);
        if(admin == null)
        {
            return "账号不存在或已冻结";
        }
        // 验证错误次数
        if(admin.getErrCount() >= 3)
        {
            // 判断上次登录时间 是否大于20分钟
            // 上次登录时间
            Date loginData = admin.getLoginDate();
            // 当前时间
            Date endDate = DateUtil.date();
            // 时间间隔
            Long interval = (endDate.getTime() - loginData.getTime()) / 1000 / 60;
            if(interval >= 20){
                // 已超过20分钟 可以继续登录操作,并清除错误次数
                Query queryUpdate   = Query.query(Criteria.where("username").is(username).and("is_delete").is(0).and("status").is(1));
                Update update = new Update();
                update.set("err_count", 0);
                mongoTemplate.updateFirst(queryUpdate, update, Admin.class);
                return null;
            }else {
                // 未达20分钟 禁止登录操作
                return "密码已输入错误" + admin.getErrCount() + " 次，请于" + (20 - interval) + " 分钟后再试";
            }
        }else {
            // 错误次数未达 3 次 允许继续操作
            return null;
        }
    }

    @Override
    public Boolean authUserAndPassword(String username, String password)
    {
        Admin admin = adminService.getUserByUserName(username);
        if(admin==null) {
            return false;
        }
        String enPassword = SecureUtil.md5(password + admin.getSalt());
        if(admin.getPassword().equals(enPassword)) {
            return true;
        }else{
            return false;
        }
    }
}
