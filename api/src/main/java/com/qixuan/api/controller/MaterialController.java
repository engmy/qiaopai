package com.qixuan.api.controller;

import com.qixuan.api.service.MaterialService;
import com.qixuan.common.utils.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "物料接口")
@RestController
@RequestMapping("api")
public class MaterialController
{
    @Autowired
    private MaterialService materialService;

    @ApiOperation(value = "物料列表")
    @GetMapping("material")
    public AjaxResult list()
    {
        return AjaxResult.success( materialService.getMaterialList());
    }
}
