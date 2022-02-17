package com.qixuan.admin.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.qixuan.admin.service.RoleMenuService;
import com.qixuan.common.entity.Admin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface
{

    @Resource
    private HttpSession session;

    @Resource
    private RoleMenuService roleMenuService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType)
    {
        Admin admin = (Admin) session.getAttribute("admin");
        return roleMenuService.getRoleMenuPermissions(admin.getRoleId());
    }

    @Override
    public List<String> getRoleList(Object o, String s)
    {
        Admin admin = (Admin) session.getAttribute("admin");
        List<String> list = new ArrayList();
        list.add(admin.getRoleId());
        return list;
    }
}
