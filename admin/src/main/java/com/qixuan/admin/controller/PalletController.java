package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.service.RelationService;
import com.qixuan.common.enums.BusinessType;
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
@RequestMapping("admin/pallet")
public class PalletController
{
    @Autowired
    private RelationService relationService;

    /**
     * 托盘数据
     */
    @SaCheckPermission("admin:pallet:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/pallet/index";
    }

    /**
     * 托盘列表
     */
    @ResponseBody
    @SaCheckPermission("admin:pallet:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult list(@RequestParam(value="page", defaultValue="1")  Integer page, @RequestParam(value="limit", defaultValue="10") Integer limit,
                           @RequestParam Map params
    )
    {
        PageHelper list = relationService.getRelationData(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 删除托盘数据
     */
    @Log(title = "删除托盘数据", businessType = BusinessType.DELETE)
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value="id") String palletCodes)
    {
        Boolean result = relationService.deleteRelation(palletCodes);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }
}
