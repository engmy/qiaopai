package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.qixuan.admin.service.LoginLogService;
import com.qixuan.common.utils.AjaxResult;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("admin/loginlog")
public class LoginLogController
{
    @Autowired
    private LoginLogService loginLogService;

    /**
     * 登录日志
     */
    @SaCheckPermission("admin:loginlog:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/loginlog/index";
    }

    /**
     * 登录日志
     */
    @ResponseBody
    @SaCheckPermission("admin:loginlog:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult list(@RequestParam(value="page", defaultValue="1") Integer page,
    @RequestParam(value="limit", defaultValue="10") Integer limit,
    @RequestParam Map params)
    {
        PageHelper list = loginLogService.getLoginLogList(page, limit, params);
        return AjaxResult.success(list);
    }
}
