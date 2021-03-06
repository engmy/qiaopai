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
            return "???????????????????????????";
        }
        // ??????????????????
        if(admin.getErrCount() >= 3)
        {
            // ???????????????????????? ????????????20??????
            // ??????????????????
            Date loginData = admin.getLoginDate();
            // ????????????
            Date endDate = DateUtil.date();
            // ????????????
            Long interval = (endDate.getTime() - loginData.getTime()) / 1000 / 60;
            if(interval >= 20){
                // ?????????20?????? ????????????????????????,?????????????????????
                Query queryUpdate   = Query.query(Criteria.where("username").is(username).and("is_delete").is(0).and("status").is(1));
                Update update = new Update();
                update.set("err_count", 0);
                mongoTemplate.updateFirst(queryUpdate, update, Admin.class);
                return null;
            }else {
                // ??????20?????? ??????????????????
                return "?????????????????????" + admin.getErrCount() + " ????????????" + (20 - interval) + " ???????????????";
            }
        }else {
            // ?????????????????? 3 ??? ??????????????????
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
