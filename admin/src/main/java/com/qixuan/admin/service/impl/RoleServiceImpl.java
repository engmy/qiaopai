package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.admin.form.RoleMenuForm;
import com.qixuan.admin.service.AdminService;
import com.qixuan.admin.service.RoleMenuService;
import com.qixuan.admin.service.RoleService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.entity.Role;
import com.qixuan.common.exception.CustomException;
import com.qixuan.common.utils.MongoUtil;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService
{
    @Resource
    private HttpSession session;

    @Resource
    private AdminService adminService;

    @Resource
    private RoleMenuService roleMenuService;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public PageHelper getRoleList(Integer page, Integer limit,  Map params)
    {
        Criteria criteria = Criteria.where("is_delete").is(0).and("user_type").is(0);
        if(StrUtil.isNotEmpty((String) params.get("role_name")))
        {
            criteria.and("role_name").regex(params.get("role_name").toString());
        }
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Order.desc("_id")));
        MongoUtil mongoUtil = new MongoUtil();
        mongoUtil.start(page, limit, query);
        List<Role> roleList = mongoTemplate.find(query, Role.class);
        long count = mongoTemplate.count(new Query(criteria), Role.class);
        PageHelper pageHelper = mongoUtil.pageHelper(count, roleList);
        return pageHelper;
    }

    @Override
    public Boolean insertRole(RoleMenuForm roleMenuForm)
    {
        if(this.existRole(roleMenuForm.getRoleName()))
        {
            throw new CustomException("角色已存在！");
        }
        Admin admin = (Admin) session.getAttribute("admin");
        Role role = new Role();
        BeanUtils.copyProperties(roleMenuForm, role);
        role.setCreateUser(admin.getUsername());
        role.setUpdateUser(admin.getUsername());
        Role roleResult = mongoTemplate.insert(role);
        if(roleResult==null) {
            return false;
        }
        Boolean roleMenuResult = roleMenuService.insertRoleMenu(roleResult.getRoleId(), roleMenuForm.getMenuIds());
        if(roleMenuResult)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean updateRole(RoleMenuForm roleMenuForm)
    {
        Admin admin = (Admin) session.getAttribute("admin");
        Query query = Query.query(Criteria.where("_id").is(roleMenuForm.getRoleId()));

        Update update = new Update();
        update.set("role_sort",     roleMenuForm.getRoleSort());
        update.set("role_name",     roleMenuForm.getRoleName());
        update.set("status",        roleMenuForm.getStatus());
        update.set("update_user",   admin.getUsername());
		update.set("update_time", 	DateUtil.date());
        UpdateResult roleResult = mongoTemplate.updateFirst(query, update, Role.class);
        if(roleResult==null) {
            return false;
        }
        Boolean deleteResult = roleMenuService.deleteRoleMenu(roleMenuForm.getRoleId());
        Boolean roleMenuResult = roleMenuService.insertRoleMenu(roleMenuForm.getRoleId(), roleMenuForm.getMenuIds());
        if(deleteResult && roleMenuResult)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean deleteRole(String roleId)
    {
        // 判断角色下是否有用户
        if (adminService.existUserByRoleId(roleId))
        {
            throw new CustomException("角色下有用户不能被删除");
        }

        Query query = Query.query(Criteria.where("_id").is(roleId));
        Update update = new Update();

        update.set("is_delete", 1);
        UpdateResult deleteResult = mongoTemplate.updateFirst(query, update, Role.class);
        if(deleteResult!=null)
        {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Role getRoleInfo(String roleId)
    {
        return mongoTemplate.findById(roleId, Role.class);
    }

    @Override
    public List<Role> getRoleList()
    {
        Query query = Query.query(Criteria.where("status").is(1).and("is_delete").is(0));
        List<Role> roleList = mongoTemplate.find(query, Role.class);
        return roleList;
    }

    @Override
    public List<Role> getRoleListNeSys()
    {
        Query query = Query.query(Criteria.where("status").is(1).and("is_delete").is(0).and("user_type").ne(1));
        List<Role> roleList = mongoTemplate.find(query, Role.class);
        return roleList;
    }

    @Override
    public Boolean existRole(String roleName)
    {
        Query query = Query.query(Criteria.where("role_name").is(roleName));
        return mongoTemplate.exists(query, Role.class);
    }
}
