package com.qixuan.api.controller;

import com.qixuan.api.annotation.Log;
import com.qixuan.api.form.WorkDocRelationForm;
import com.qixuan.api.service.WorkDocRelationService;
import com.qixuan.api.service.WorkDocService;
import com.qixuan.api.vo.WorkDocDetailVo;
import com.qixuan.api.vo.WorkDocStatsVo;
import com.qixuan.common.entity.WorkDoc;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

@Slf4j
@Api(tags = "工单接口")
@Validated
@RestController
@RequestMapping("api")
public class WorkDocController
{
    @Resource
    private WorkDocService workDocService;

    @Resource
    private WorkDocRelationService workDocRelationService;

    @ApiOperation(value = "工单列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name="page",       value="页码",    required = true, dataType="String", paramType = "query"),
        @ApiImplicitParam(name="limit",      value="每页条数", required = true, dataType="String", paramType = "query"),
        @ApiImplicitParam(name="start_time", value="开始时间", required = true, dataType="String", paramType = "query"),
        @ApiImplicitParam(name="end_time",   value="结束时间", required = true, dataType="String", paramType = "query"),
        @ApiImplicitParam(name="site_no",    value="工厂代码", required = true, dataType="String", paramType = "query"),
        @ApiImplicitParam(name="pline_no",   value="产线代码", required = true, dataType="String", paramType = "query")
    })
    @GetMapping("workdoc")
    public AjaxResult list(@RequestParam(value="page", defaultValue="1") Integer page,
    @RequestParam(value="limit", defaultValue="1000") Integer limit,
    @RequestParam(value="start_time", defaultValue = "") String startTime,
    @RequestParam(value="end_time",   defaultValue = "") String endTime,
    @RequestParam(value="site_no",    defaultValue = "") String siteNo,
    @RequestParam(value="pline_no",   defaultValue = "") String plineNo)
    {
        return AjaxResult.success(workDocService.getWorkDocList(page, limit, startTime, endTime, siteNo, plineNo));
    }

    @Log(title = "工单信息", businessType = BusinessType.SELECT)
    @ApiOperation(value = "工单信息")
    @ApiImplicitParam(name="doc_no", value="工单号|罐装单号", required = true, dataType="String", paramType = "query")
    @GetMapping("workdoc/{doc_no}")
    public AjaxResult getWorkDocByDocNo(@PathVariable("doc_no") String docNo)
    {
        WorkDoc result = workDocService.getWorkDocInfo(docNo, 2);
        if(result!=null)
        {
            return AjaxResult.success(result);
        }else{
            return AjaxResult.error();
        }
    }

    @Log(title = "修改工单", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name="doc_no",     value="工单号", required = true, dataType="String", paramType = "query"),
        @ApiImplicitParam(name="doc_status", value="状态",   required = true, dataType="String", paramType = "query")
    })
    @PutMapping("workdoc")
    public AjaxResult setWorkDocStatus(@RequestParam(name = "doc_no") String docNo, @RequestParam("doc_status") String docStatus)
    {
        Boolean result = workDocService.updateWorkDocStatus(docNo, docStatus);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    @Log(title = "工单上传", businessType = BusinessType.INSERT)
    @ApiOperation(value = "工单上传")
    @PostMapping("workdoc/upload")
    public AjaxResult upLoadWorkDocData(@RequestBody @Validated WorkDocRelationForm workDocRelationForm)
    {
        Boolean result = workDocRelationService.uploadWorkDocRelation(workDocRelationForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    @Log(title = "工单上传", businessType = BusinessType.SELECT)
    @ApiOperation(value = "工单统计")
    @ApiImplicitParam(name="doc_no", value="工单号", required = true, dataType="String", paramType = "query")
    @GetMapping("workdoc/stats")
    public AjaxResult queryWorkDocUploadStatusByDocNo(@NotBlank(message = "工单号不能为空") String doc_no)
    {
        WorkDocStatsVo workDocStats = workDocRelationService.getWorkDocStats(doc_no);
        return AjaxResult.success(workDocStats);
    }

    @Log(title = "工单明细", businessType = BusinessType.SELECT)
    @ApiOperation(value = "工单明细")
    @ApiImplicitParam(name="doc_no", value="工单号", required = true, dataType="String", paramType = "query")
    @GetMapping("workdoc/detail")
    public AjaxResult queryWorkDocAllCartonByDocNo(@NotBlank(message = "工单号不能为空") String doc_no)
    {
        WorkDocDetailVo workDocDetail = workDocRelationService.getWorkDocDetail(doc_no);
        return AjaxResult.success(workDocDetail);
    }
}
