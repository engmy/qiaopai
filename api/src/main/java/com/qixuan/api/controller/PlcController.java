package com.qixuan.api.controller;

import com.qixuan.api.service.PlcService;
import com.qixuan.api.vo.CallVo;
import com.qixuan.common.utils.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Api(tags = "通信接口")
@Validated
@RestController
@RequestMapping("api")
public class PlcController
{
    @Autowired
    private PlcService plcService;

    @ApiOperation(value = "通讯测试")
    @ApiImplicitParam(name="ping_val", value="入参", dataType="String", paramType = "query")
    @PostMapping("plc/check")
    public AjaxResult check(@NotBlank(message = "入参不能为空") String ping_val)
    {
        CallVo callVo = new CallVo();
        BeanUtils.copyProperties(plcService.check(ping_val), callVo);
        return AjaxResult.success(callVo);
    }
}
