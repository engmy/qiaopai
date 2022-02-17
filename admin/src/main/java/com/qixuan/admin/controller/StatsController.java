package com.qixuan.admin.controller;

import com.qixuan.admin.service.StatsService;
import com.qixuan.common.utils.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 工单管理
 */
@Controller
@RequestMapping(value = "/admin/stats")
public class StatsController
{
    @Autowired
    private StatsService statsService;

    /**
     * 工单统计
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/stats/index";
    }

    /**
     * 工单统计
     */
    @ResponseBody
    @RequestMapping(value = "/data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult data(@RequestParam(value="page",  defaultValue="1")  Integer page,
                           @RequestParam(value="limit", defaultValue="10") Integer limit,
                           @RequestParam Map params
    )
    {
        return AjaxResult.success(statsService.getStatsList(page, limit, params));
    }
}