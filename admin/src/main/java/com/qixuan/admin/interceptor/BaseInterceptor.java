package com.qixuan.admin.interceptor;

import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.service.ConfigService;
import com.qixuan.admin.service.MenuService;
import com.qixuan.admin.service.RoleMenuService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.enums.GroupConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Component
public class BaseInterceptor implements HandlerInterceptor 
{
	@Resource
	private HttpSession session;

	@Value("${spring.application.name}")
	private String website;

	@Value("${spring.application.copyright}")
	private String copyright;

	@Resource
	private MenuService menuService;

	@Resource
	private ConfigService configService;

	@Resource
	private RoleMenuService roleMenuService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	{
		Admin admin = (Admin) session.getAttribute("admin");
		Map<String, String> system = configService.getConfigMapByGroupId(GroupConfig.SYSTEM.getCode());

		// 系统名称
		request.setAttribute("website", StrUtil.isNotEmpty(system.get("application")) ? system.get("application") : website);

		// 系统版权
		request.setAttribute("copyright", StrUtil.isNotEmpty(system.get("copyright")) ? system.get("copyright") : copyright);

		// 系统菜单
		if(admin!=null)
		{
			request.setAttribute("menuList", menuService.getTreeRoleMenuList(admin.getRoleId()));
			request.setAttribute("permissions", roleMenuService.getRoleMenuPermissions(admin.getRoleId()));
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
	}
}