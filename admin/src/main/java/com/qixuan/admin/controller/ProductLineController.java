package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.form.ProductLineForm;
import com.qixuan.admin.form.ValidGroups;
import com.qixuan.admin.service.ConfigService;
import com.qixuan.admin.service.ProductLineService;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("admin/pline")
public class ProductLineController
{
    @Autowired
    private ConfigService configService;

    @Autowired
    private ProductLineService productLineService;

    /**
     * 产线管理
     */
    @SaCheckPermission("admin:pline:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/pline/index";
    }

    /**
     * 产线列表
     */
    @ResponseBody
    @SaCheckPermission("admin:pline:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult list(@RequestParam(value="page", defaultValue="1") Integer page, @RequestParam(value="limit", defaultValue="10") Integer limit,
                           @RequestParam Map params
    )
    {
        PageHelper list = productLineService.getProductLineList(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 产线代码
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult search(@RequestParam Map params)
    {
        return AjaxResult.success(productLineService.searchProductLineList());
    }

    /**
     * 产线角色
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(ModelMap map)
    {
        map.put("siteNo", configService.getConfigByKey("site_no"));
        return "admin/pline/add";
    }

    /**
     * 保存表单数据
     */
    @Log(title = "产线管理", businessType = BusinessType.INSERT)
    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult insert(@Validated(ValidGroups.Insert.class) ProductLineForm productLineForm)
    {
        Boolean result = productLineService.insertProductLine(productLineForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 删除产线
     */
    @Log(title = "产线管理", businessType = BusinessType.DELETE)
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value="id") String plineId)
    {
        Boolean result = productLineService.deleteProductLine(plineId);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }
}
