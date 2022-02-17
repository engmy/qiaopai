package com.qixuan.admin.service;

import com.qixuan.admin.form.AdminForm;
import com.qixuan.admin.form.EditPassForm;
import com.qixuan.admin.form.ProfileForm;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.utils.PageHelper;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AdminService
{
	// 用户列表
	PageHelper getUserList(Integer page, Integer limit, Map params);

	// 新增用户
	Boolean insertUser(AdminForm adminForm);

	// 更新用户
	Boolean updateUser(AdminForm adminForm);

	// 判断用户名是否存在
	Boolean existUser(String username);

	// 判断手机号是否存在
	Boolean existPhone(String phone);

	// 判断邮箱地址是否存在 可为空，为空不判断
	Boolean existEmail(String email);

	// 获取用户信息
	Admin getUserByUserName(String username);

	// 修改密码接口
	Boolean editPass(String username, String newpass);

	// 删除用户
	Boolean deleteUser(String id);

	// 获取用户详情
	Admin getUserInfo(String id);

	// 查询角色下用户
	Boolean existUserByRoleId(String roleId);

	// 更新用户资料
	Boolean updateProfile(ProfileForm profileForm);

	// 管理员修改用户密码
	Boolean editPassWord(EditPassForm editPassForm);

	// 更新用户状态
	Boolean updateStatus(String id,Integer status);

	// 更新用户的头像
	Boolean updateAvatar(String path, MultipartFile file);
}
