package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.service.ProductLogService;
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
@RequestMapping("admin/prolog")
public class ProductLogController
{
    @Autowired
    private ProductLogService productLogService;

    /**
     * 操作记录
     */
    @SaCheckPermission("admin:prolog:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/prolog/index";
    }

    /**
     * 操作记录
     */
    @ResponseBody
    @SaCheckPermission("admin:prolog:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult list(@RequestParam(value="page", defaultValue="1") Integer page,
    @RequestParam(value="limit", defaultValue="10") Integer limit,
    @RequestParam Map params)
    {
        PageHelper list = productLogService.getProductLogList(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 日志详情
     */
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String edit(@RequestParam(value="id") String logId, ModelMap map)
    {
        if(StrUtil.isNotEmpty(logId))
        {
            map.addAttribute("info", productLogService.getProductLogDetail(logId));
        }
        return "admin/prolog/detail";
    }
}
