package com.qixuan.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.form.LoginForm;
import com.qixuan.admin.service.AuthService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.utils.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@Controller
public class LoginController
{
	@Autowired
	private AuthService authService;

	/**
	 * 系统首页
	 * @return string
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public void index(HttpServletResponse response) throws IOException
	{
		response.sendRedirect("/admin/login");
	}

	/**
	 * 管理员登录试图
	 * 
	 * @return string
	 */
	@RequestMapping(value = "/admin/login", method = RequestMethod.GET)
	public String index()
	{
		return "admin/login";
	}

	/**
	 * 管理员登录操作
	 * @param loginForm
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/admin/dologin", method = RequestMethod.POST, headers = "Accept=application/json")
	public AjaxResult login(@Validated LoginForm loginForm, HttpSession session)
	{
        String code = (String) session.getAttribute("captcha");
        if (StrUtil.isEmpty(code) || !code.equals(loginForm.getCaptcha()))
        {
            return AjaxResult.error(401, "验证码错误");
        }
        // 密码输入错误次数与时间验证
        String isError = authService.isErrLogion(loginForm.getUsername());
        if(!StrUtil.isEmpty(isError)){
			return AjaxResult.error(isError);
		}

		Admin admin = authService.userLogin(loginForm.getUsername(), loginForm.getPassword());
		if(admin != null)
		{
			// 默认弱密码
			Boolean passwordStrength = false;

			// 默认弱密码
			Boolean passwordLength = false;

			// 密码超过三个月未修改 强制修改密码
			Boolean modifyExpiration = false;

			if(loginForm.getPassword().equals("123456"))
			{
				// 验证密码是否是初始密码
				passwordStrength = true;
			}else if(loginForm.getPassword().length() < 12)
			{
				// 密码长度小于12位
				passwordLength = true;
			}else if(this.intervalTime(admin))
			{
				// 密码超过三个月未修改 强制修改密码
				modifyExpiration = true;
			}

			StpUtil.login(admin.getUserId());
			session.setAttribute("admin", admin);
			session.setAttribute("passwordStrength", passwordStrength);
			session.setAttribute("passwordLength",   passwordLength);
			session.setAttribute("modifyExpiration", modifyExpiration);
			return AjaxResult.success("登录成功");
		}else{
			return AjaxResult.error("账号或密码错误");
		}
	}

	private Boolean intervalTime(Admin admin)
	{
		// 上次密码修改时间
		Date loginData = admin.getUpdatePwdTime();
		// 当前时间
		Date endDate = DateUtil.date();
		// 时间间隔 三个月需要修改异常密码
		Long interval = (endDate.getTime() - loginData.getTime()) / 1000 / 60 / 60 / 24 / 30;

		return interval >= 3;
	}

	/**
	 * 登录验证码
	 * 
	 * @return String
	 */
	@RequestMapping(value = "/admin/captcha", method = RequestMethod.GET)
	public void captcha(HttpServletResponse response, HttpSession session)
	{
		LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(116, 38,4,10);
		session.setAttribute("captcha", lineCaptcha.getCode());
		try
		{
			ServletOutputStream outputStream = response.getOutputStream();
			lineCaptcha.write(outputStream);
			outputStream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
