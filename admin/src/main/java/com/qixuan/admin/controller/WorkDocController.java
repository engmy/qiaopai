package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.form.ValidGroups;
import com.qixuan.admin.form.WorkDocForm;
import com.qixuan.admin.service.ProductLineService;
import com.qixuan.admin.service.WorkDocService;
import com.qixuan.admin.vo.WorkDocVo;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工单管理
 */
@Controller
@RequestMapping(value = "/admin/workdoc")
public class WorkDocController
{
    @Autowired
    private WorkDocService workDocService;

    @Autowired
    private ProductLineService productLineService;

    @Value("${custom.upload.export-excel-file-path}")
    private String exportExcelFilePath;

    /**
     * 工单列表
     */
    @SaCheckPermission("admin:workdoc:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search, ModelMap map)
    {
        search.put("params", params);
        map.put("lineList", productLineService.searchProductLineList());
        return "admin/workdoc/index";
    }

    /**
     * 工单列表
     */
    @ResponseBody
    @SaCheckPermission("admin:workdoc:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult data(@RequestParam(value="page", defaultValue="1") Integer page,
                           @RequestParam(value="limit", defaultValue="10") Integer limit,
                           @RequestParam Map params
    )
    {
        return AjaxResult.success(workDocService.getWorkDocList(page, limit, params));
    }

    /**
     * 新增工单
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(ModelMap map)
    {
        map.put("lineList", productLineService.searchProductLineList());
        return "admin/workdoc/add";
    }

    /**
     * 编辑工单
     */
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(@RequestParam(value="id") String id, ModelMap map)
    {
        map.put("info", workDocService.getWorkDocInfo(id));
        map.put("lineList", productLineService.searchProductLineList());
        return "admin/workdoc/edit";
    }

    /**
     * 新增工单
     */
    @Log(title = "工单管理", businessType = BusinessType.INSERT)
    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult insert(@Validated(ValidGroups.Insert.class) WorkDocForm workDocForm)
    {
        Boolean result = workDocService.insertWorkDoc(workDocForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 修改工单
     */
    @Log(title = "工单管理", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult update(@Validated(ValidGroups.Update.class) WorkDocForm workDocForm)
    {
        Boolean result = workDocService.updateWorkDoc(workDocForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 删除工单
     */
    @Log(title = "工单管理", businessType = BusinessType.DELETE)
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value="id") String id)
    {
        Boolean result = workDocService.deleteWorkDoc(id);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 导出工单
     */
    @Log(title = "导出工单", businessType = BusinessType.EXPORT)
    @ResponseBody
    @RequestMapping(value = "export", method = RequestMethod.POST, headers = "Accept=application/json")
    public void export(@RequestParam Map params, HttpServletResponse response)
    {
        List<WorkDocVo> workDocVoList = workDocService.getWorkDocList(params);

        Collection cl = new ArrayList();
        List<?> row1 = CollUtil.newArrayList(
            "工单号",
            "产品编码",
            "产品名称",
            "灌装单号",
            "产品批号",
            "产线代码",
            "生产日期",
            "计划数量",
            "实际数量",
            "已上传数量",
            "单据状态",
            "创建方式",
            "更新人",
            "更新时间"
        );

        cl.add(row1);
        CollUtil.newArrayList(row1);
        for (WorkDocVo workDocVo : workDocVoList)
        {
            List<?> row = CollUtil.newArrayList(
                workDocVo.getDocNo(),
                workDocVo.getSkuNo(),
                workDocVo.getSkuNo(),
                workDocVo.getExtend2(),
                workDocVo.getLotNo(),
                workDocVo.getPlineNo(),
                workDocVo.getMfgDate(),
                workDocVo.getReqQty(),
                workDocVo.getQty1(),
                workDocVo.getQty2(),
                workDocVo.getDocStatus(),
                workDocVo.getSource()==1?"接口":"手动",
                workDocVo.getUpdateUser(),
                workDocVo.getUpdateTime()
            );
            cl.add(row);
        }

        List<List<?>> rows = CollUtil.newArrayList(cl);
        String fileName = "生产工单" + new SimpleDateFormat("yyyyMMdd").format(new Date())+ RandomUtil.randomNumbers(6);
        String filePath = exportExcelFilePath + "/" + fileName + ".xlsx";
        BigExcelWriter writer = ExcelUtil.getBigWriter(filePath);

        writer.setColumnWidth(0, 20);
        writer.setColumnWidth(1, 20);
        writer.setColumnWidth(2, 20);
        writer.setColumnWidth(3, 20);
        writer.setColumnWidth(4, 20);
        writer.setColumnWidth(5, 20);
        writer.setColumnWidth(6, 20);
        writer.setColumnWidth(7, 20);
        writer.setColumnWidth(8, 20);
        writer.setColumnWidth(9, 20);
        writer.setColumnWidth(10, 20);
        writer.setColumnWidth(11, 20);
        writer.setColumnWidth(12, 20);
        writer.setColumnWidth(13, 20);
        writer.write(rows);

        try
        {
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition","attachment;filename=test.xls");
            ServletOutputStream out = response.getOutputStream();
            writer.flush(out, true);
            writer.close();
            IoUtil.close(out);
            return;
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}