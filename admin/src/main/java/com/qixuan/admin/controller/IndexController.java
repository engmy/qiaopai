package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.service.AdminService;
import com.qixuan.admin.service.AuthService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin")
public class IndexController
{
	@Autowired
	private AuthService authService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private HttpSession session;

	@SaCheckPermission("admin:index")
	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String index(@RequestParam Map params, ModelMap search)
	{
		search.put("params", params);
		return "admin/index";
	}

	/**
	 * 修改密码试图
	 * @return
	 */
	@RequestMapping(value = "editPass", method = RequestMethod.GET)
	public String editPass() {
		return "admin/editpass";
	}

	/**
	 * 登录也修改密码试图
	 * @return
	 */
	@RequestMapping(value = "editPwd", method = RequestMethod.GET)
	public String editpwd() {
		return "admin/public/editpwd";
	}

	/**
	 * 修改密码操作
	 * @return JSONObject
	 */
	@Log(title = "修改密码", businessType = BusinessType.UPDATE)
	@ResponseBody
	@RequestMapping(value = "editPass", method = RequestMethod.POST, headers = "Accept=application/json")
	public AjaxResult editPass(HttpServletRequest request, Model model, SessionStatus sessionStatus)
	{
		String password  = request.getParameter("password");
		String newpass   = request.getParameter("newpass");
		String renewpass = request.getParameter("renewpass");
		
		HttpSession session = request.getSession();
		Admin admin = (Admin) session.getAttribute("admin");
		
		if(admin!=null)
		{
			String username = admin.getUsername();
			Boolean result  = authService.authUserAndPassword(username, password);
			
			if(!result) {
				return AjaxResult.error(400, "原密码错误");
			}
			if (password.equals(newpass)) {
				return AjaxResult.error(400, "新密码不能和原密码相同");
			}
			if (!newpass.equals(renewpass)) {
				return AjaxResult.error(400, "两次输入的新密码不一致");
			}

			Boolean editResult = adminService.editPass(username, renewpass);
			if (editResult)
			{
				StpUtil.logout();
				sessionStatus.setComplete();
				session.removeAttribute("admin");
				return AjaxResult.success("修改成功，请重新登录");
			}
		}
		return AjaxResult.error(400, "修改失败");
	}

	/**
	 * 登录页 强制修改密码操作
	 * @return JSONObject
	 */
	@Log(title = "修改密码", businessType = BusinessType.UPDATE)
	@ResponseBody
	@RequestMapping(value = "edit-logon-pwd", method = RequestMethod.POST, headers = "Accept=application/json")
	public AjaxResult editpwd(HttpServletRequest request)
	{
		String password  = request.getParameter("password");
		String newpass   = request.getParameter("newpass");
		String renewpass = request.getParameter("renewpass");
		String username =  request.getParameter("username");

		Boolean result  = authService.authUserAndPassword(username, password);

		if(!result) {
			return AjaxResult.error(400, "原密码错误");
		}
		if (password.equals(newpass)) {
			return AjaxResult.error(400, "新密码不能和原密码相同");
		}
		if (!newpass.equals(renewpass)) {
			return AjaxResult.error(400, "两次输入的新密码不一致");
		}

		Boolean editResult = adminService.editPass(username, renewpass);
		if (editResult)
		{
			return AjaxResult.success("修改成功");
		}

		return AjaxResult.error(400, "修改失败");
	}


	/**
	 * 退出管理员登录
	 * 
	 * @return String
	 */
	@ResponseBody
	@RequestMapping("logout")
	public AjaxResult logout(SessionStatus sessionStatus)
	{
		StpUtil.logout();
		sessionStatus.setComplete();
		session.removeAttribute("admin");

		if (session.getAttribute("admin") == null)
		{
			return AjaxResult.success("退出成功", "/admin/login");
		}
		return AjaxResult.error(400, "退出失败");
	}
}
