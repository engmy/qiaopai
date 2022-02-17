package com.qixuan.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.mongodb.client.result.UpdateResult;
import com.qixuan.admin.form.AdminForm;
import com.qixuan.admin.form.EditPassForm;
import com.qixuan.admin.form.ProfileForm;
import com.qixuan.admin.service.AdminService;
import com.qixuan.admin.service.RoleService;
import com.qixuan.admin.vo.AdminVo;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService
{
	@Resource
	private HttpSession session;

	@Resource
	private MongoTemplate mongoTemplate;

	@Resource
	private RoleService roleService;

	@Override
	public PageHelper getUserList(Integer page, Integer limit, Map params)
	{
		Criteria criteria = Criteria.where("is_delete").is(0).and("user_type").is(0);
		if(StrUtil.isNotEmpty((String) params.get("username")))
		{
			criteria.and("username").regex(params.get("username").toString());
		}
		Query query = Query.query(criteria);
		query.with(Sort.by(Sort.Order.desc("_id")));
		MongoUtil mongoUtil = new MongoUtil();
		mongoUtil.start(page, limit, query);
		List<AdminVo> adminVoList = mongoTemplate.find(query, AdminVo.class);

		for(int i=0; i < adminVoList.size(); i++)
		{
			Role role = roleService.getRoleInfo(adminVoList.get(i).getRoleId());
			adminVoList.get(i).setRoleName(role.getRoleName());
		}
		long count = mongoTemplate.count(new Query(criteria), Admin.class);
		PageHelper pageHelper = mongoUtil.pageHelper(count, adminVoList);
		return pageHelper;
	}

	@Override
	public Boolean insertUser(AdminForm adminForm)
	{
		Admin user = new Admin();
		// 创建用户时将密码修改时间设置为当前
		user.setUpdatePwdTime(new Date());
		BeanUtils.copyProperties(adminForm, user);
		String salt = RandomUtil.randomString(6);

		user.setSalt(salt);
		user.setPassword(SecureUtil.md5(adminForm.getPassword() + salt));

		Admin userResult = mongoTemplate.insert(user);
		if(userResult!=null) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean updateUser(AdminForm adminForm)
	{
		Admin admin = (Admin) session.getAttribute("admin");
		Query query = Query.query(Criteria.where("_id").is(adminForm.getUserId()));
		Update update = new Update();

		update.set("email",     	adminForm.getEmail());
		update.set("phone",     	adminForm.getPhone());
		update.set("username",  	adminForm.getUsername());
		update.set("nickname",  	adminForm.getNickname());
		update.set("sex",      		adminForm.getSex());
		update.set("status",  		adminForm.getStatus());
		update.set("role_id",  		adminForm.getRoleId());
		update.set("update_user",   admin.getUsername());
		update.set("update_time", 	DateUtil.date());

		UpdateResult userResult = mongoTemplate.updateFirst(query, update, Admin.class);
		if(userResult!=null) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean existUser(String userName)
	{
		Query query = Query.query(Criteria.where("username").is(userName).and("is_delete").is(0));
		return mongoTemplate.exists(query, Admin.class);
	}

	@Override
	public Boolean existPhone(String phone)
	{
		Query query = Query.query((Criteria.where("phone").is(phone).and("is_delete").is(0)));
		return mongoTemplate.exists(query, Admin.class);
	}

	@Override
	public Boolean existEmail(String email)
	{
		if(StrUtil.hasEmpty((email)))
		{
			return false;
		}
		Query query = Query.query((Criteria.where("email").is(email).and("is_delete").is(0)));
		return mongoTemplate.exists(query, Admin.class);
	}

	@Override
	public Admin getUserByUserName(String username)
	{
		Query query = Query.query(Criteria.where("username").is(username).and("is_delete").is(0).and("status").is(1));
		return mongoTemplate.findOne(query, Admin.class);
	}

	@Override
	public Boolean editPass(String username, String newpass)
	{
		Admin admin = (Admin) session.getAttribute("admin");
		Query query   = Query.query(Criteria.where("username").is(username).and("is_delete").is(0).and("status").is(1));

		Update update = new Update();
		String salt = RandomUtil.randomString(6);
		update.set("salt", salt);
		update.set("update_pwd_time", DateUtil.date());
		update.set("password", SecureUtil.md5(newpass + salt));
		if(admin == null){
			// 此时是用户在登录页进行的强制密码修改
			update.set("update_user", username);
		}else {
			update.set("update_user", admin.getUsername());
		}

		update.set("update_time", DateUtil.date());

		UpdateResult editResult = mongoTemplate.updateFirst(query, update, Admin.class);
		if(editResult!=null) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean deleteUser(String userId)
	{
		Admin admin = this.getUserInfo(userId);
		if (admin.getUserType()==1)
		{
			throw new CustomException("系统用户不能删除！");
		}

		Query query = Query.query(Criteria.where("_id").is(userId));
		Update update = new Update();

		update.set("is_delete",   1);
		update.set("update_user", admin.getUsername());
		update.set("update_time", DateUtil.date());
		UpdateResult deleteResult = mongoTemplate.updateFirst(query, update, Admin.class);
		if(deleteResult!=null) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Admin getUserInfo(String userId)
	{
		return mongoTemplate.findById(userId, Admin.class);
	}

	@Override
	public Boolean existUserByRoleId(String roleId)
	{
		Query query = Query.query(Criteria.where("role_id").is(roleId).and("is_delete").is(0));
		return mongoTemplate.exists(query, Admin.class);
	}

	@Override
	public Boolean updateProfile(ProfileForm profileForm)
	{
		Admin admin = (Admin) session.getAttribute("admin");
		Query query = Query.query(Criteria.where("_id").is(admin.getUserId()));
		Update update = new Update();
		update.set("email",     	profileForm.getEmail());
		update.set("phone",     	profileForm.getPhone());
		update.set("nickname",  	profileForm.getNickname());
		update.set("sex",      		profileForm.getSex());
		update.set("update_user",   admin.getUsername());
		update.set("update_time",   DateUtil.date());

		UpdateResult userResult = mongoTemplate.updateFirst(query, update, Admin.class);
		if(userResult!=null) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean editPassWord(EditPassForm editPassForm)
	{
		if(!editPassForm.getNewpass().equals(editPassForm.getRenewpass()))
		{
			throw new CustomException("两次输入的密码不一样！");
		}

		Admin admin = (Admin) session.getAttribute("admin");
		Query query = Query.query(Criteria.where("_id").is(editPassForm.getUserId()));

		Update update = new Update();
		String salt = RandomUtil.randomString(6);
		update.set("salt", salt);
		update.set("password", SecureUtil.md5(editPassForm.getNewpass() + salt));
		update.set("update_user", admin.getUsername());
		update.set("update_time", DateUtil.date());
		update.set("update_pwd_time", DateUtil.date());

		UpdateResult editResult = mongoTemplate.updateFirst(query, update, Admin.class);
		if(editResult!=null) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean updateStatus(String id,Integer status)
	{
		Admin admin = (Admin) session.getAttribute("admin");
		Query query = Query.query(Criteria.where("_id").is(id));

		Update update = new Update();
		update.set("status", status);
		update.set("update_user", admin.getUsername());
		update.set("update_time", DateUtil.date());
		UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Admin.class);

		if(updateResult != null){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public Boolean updateAvatar(String path, MultipartFile file)
	{
		try {
			String fileName = System.currentTimeMillis() + "";
			String destFileName = path + File.separator + fileName + ".png";
			File destFile = new File(destFileName);
			destFile.getParentFile().mkdirs();
			file.transferTo(destFile);

			Admin admin = (Admin) session.getAttribute("admin");
			Query query = Query.query(Criteria.where("_id").is(admin.getUserId()));

			Update update = new Update();
			update.set("avatar", destFileName);
			update.set("update_user", admin.getUsername());
			update.set("update_time", DateUtil.date());
			UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Admin.class);

			if(updateResult != null){
				return true;
			}else {
				return false;
			}
		} catch (IOException e)
		{
			return false;
		}
	}
}