package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.qixuan.admin.service.RelationService;
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
@RequestMapping("admin/relation")
public class RelationController
{
    @Autowired
    private RelationService relationService;

    /**
     * 生产管理
     */
    @SaCheckPermission("admin:relation:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/relation/index";
    }

    /**
     * 生产列表
     */
    @ResponseBody
    @SaCheckPermission("admin:relation:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult list(@RequestParam(value="page", defaultValue="1")  Integer page, @RequestParam(value="limit", defaultValue="10") Integer limit,
                           @RequestParam Map params
    )
    {
        PageHelper list = relationService.getRelationList(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 工单明细
     */
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/relation/detail";
    }
}
