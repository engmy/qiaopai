package com.qixuan.admin.config;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.qixuan.admin.interceptor.BaseInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Slf4j
@Configuration
public class AdminConfig implements WebMvcConfigurer
{
	@Autowired
	private BaseInterceptor baseInter;

    @Override
    public void addInterceptors(InterceptorRegistry registry)
	{
		// 登录拦截器
		registry.addInterceptor(new SaRouteInterceptor((req, res, handler) ->
		{
			if(!req.getMethod().equals("OPTIONS"))
			{
				SaRouter.match(Arrays.asList("/admin/**"), Arrays.asList("/admin/login", "/admin/captcha", "/admin/dologin", "/admin/js/**", "/admin/css/**", "/admin/img/**", "/admin/layui/**", "/admin/edit-logon-pwd"), () -> StpUtil.checkLogin());
			}
		})).addPathPatterns("/**");

		// 参数拦截器
		registry.addInterceptor(baseInter);

		// 鉴权注解拦截器
		registry.addInterceptor(new SaAnnotationInterceptor());
    }
}
