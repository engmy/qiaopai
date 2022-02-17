package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.form.MaterialForm;
import com.qixuan.admin.form.ValidGroups;
import com.qixuan.admin.service.ExcelService;
import com.qixuan.admin.service.MaterialService;
import com.qixuan.common.entity.Material;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import com.qixuan.common.utils.PageHelper;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 物料管理
 */
@Controller
@RequestMapping(value = "/admin/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Resource
    private ExcelService excelService;

    /**
     * 物料列表
     */
    @SaCheckPermission("admin:material:index")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search) {
        search.put("params", params);
        return "admin/material/index";
    }

    /**
     * 物料列表
     */
    @ResponseBody
    @SaCheckPermission("admin:material:data")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult data(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                           @RequestParam Map params
    ) {
        PageHelper list = materialService.getMaterialList(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 物料搜索
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult search() {
        return AjaxResult.success(materialService.searchMaterialList());
    }

    /**
     * 新增物料
     */
    @SaCheckPermission("admin:material:add")
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add() {
        return "admin/material/add";
    }

    /**
     * 编辑物料
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(@RequestParam(value = "id") String id, Model map) {
        if (StrUtil.isNotEmpty(id)) {
            map.addAttribute("info", materialService.getMaterialInfo(id));
        }
        return "admin/material/edit";
    }

    /**
     * 新增物料
     */
    @Log(title = "物料管理", businessType = BusinessType.INSERT)
    @ResponseBody
    @SaCheckPermission("admin:material:add")
    @RequestMapping(value = "insert", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult insert(@Validated(ValidGroups.Insert.class) MaterialForm materialForm) {
        if (materialService.existMaterial(materialForm.getSkuNo())) {
            return AjaxResult.error("产品编码已存在!");
        }
        Boolean result = materialService.insertMaterial(materialForm);
        if (result) {
            return AjaxResult.success();
        } else {
            return AjaxResult.error();
        }
    }

    /**
     * 编辑物料
     */
    @Log(title = "物料管理", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult update(@Validated(ValidGroups.Update.class) MaterialForm materialForm) {
        Boolean result = materialService.updateMaterial(materialForm);
        if (result) {
            return AjaxResult.success();
        } else {
            return AjaxResult.error();
        }
    }

    /**
     * 删除物料
     */
    @Log(title = "物料管理", businessType = BusinessType.DELETE)
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value = "id") String id) {
        Boolean result = materialService.deleteMaterial(id);
        if (result) {
            return AjaxResult.success();
        } else {
            return AjaxResult.error();
        }
    }

    /**
     * 导入物料
     *
     * @param file
     * @return
     */
    @Log(title = "物料管理", businessType = BusinessType.IMPORT)
    @ResponseBody
    @SaCheckPermission("admin:material:excel")
    @RequestMapping(value = "excel", method = RequestMethod.POST)
    public AjaxResult excel(@RequestParam(value = "file") MultipartFile file) {
        // 获取文件上传流
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            return AjaxResult.error("导入失败");
        }

        ExcelReader excelReader = ExcelUtil.getReader(inputStream, 0);
        if (excelReader.getRowCount() <= 1) {
            return AjaxResult.error("文件没有数据");
        }

        Integer success = 0;
        Integer error = 0;

        List<List<Object>> read = excelReader.read(1, excelReader.getRowCount());
        System.out.println(excelReader.getRowCount());

        for (List<Object> objects : read) {
            if (ObjectUtil.length(objects) == 0) {
                continue;
            }

            String skuDescEn = objects.get(1).toString();
            String skuDescCN = objects.get(2).toString();

            if (ObjectUtil.contains(skuDescEn, "18L") || ObjectUtil.contains(skuDescEn, "20L") || ObjectUtil.contains(skuDescEn, "200L") || ObjectUtil.contains(skuDescEn, "209L") || ObjectUtil.contains(skuDescEn, "1000L")) {
                String skuNo = objects.get(0).toString().trim();

                Material material = materialService.getMaterialInfoBySkuNo(skuNo);
                if (ObjectUtil.isNotNull(material)) {
                    continue;
                }

                String packages = "";
                Integer carton = 0;
                if (ObjectUtil.contains(skuDescEn, "18L")) {
                    carton = 36;
                    packages = "18L";
                }
                if (ObjectUtil.contains(skuDescEn, "20L")) {
                    carton = 36;
                    packages = "20L";
                }
                if (ObjectUtil.contains(skuDescEn, "200L")) {
                    carton = 4;
                    packages = "200L";
                }
                if (ObjectUtil.contains(skuDescEn, "209L")) {
                    carton = 4;
                    packages = "209L";
                }
                if (ObjectUtil.contains(skuDescEn, "1000L")) {
                    carton = 1;
                    packages = "1000L";
                }

                String midNameEn = skuDescEn;
                String midNameCh = ObjectUtil.contains(skuDescCN, "#N/A") ? skuDescEn : skuDescCN;
                String family = "";
                String productName = "";
                String productGroup = "";
                String subGroup = "";
                String labellingType = "";
                String sourceType = "";

                MaterialForm materialForm = new MaterialForm();
                materialForm.setSkuNo(skuNo);
                materialForm.setSkuDesc(midNameCh);
                materialForm.setSkuSpec(packages);
                materialForm.setPallet2carton(carton);
                materialForm.setCartonUnit("桶");

                materialForm.setMidNameEng(midNameEn);
                materialForm.setMidNameChn(midNameCh);
                materialForm.setFamily(family);
                materialForm.setProductName(midNameCh);
                materialForm.setProductGroup(productGroup);
                materialForm.setSubGroup(subGroup);
                materialForm.setPackages(packages);
                materialForm.setLabellingType(labellingType);
                materialForm.setSourceType(sourceType);

                Boolean result = materialService.insertMaterial(materialForm);
                if (result) {
                    success++;
                } else {
                    error++;
                }
            }
        }
        return AjaxResult.success("导入成功" + success + "条，导入失败：" + error + "条");
    }

    /**
     * 导入物料
     *
     * @param file
     * @return
     */
    @Log(title = "物料管理", businessType = BusinessType.IMPORT)
    @ResponseBody
    @SaCheckPermission("admin:material:excel")
    @RequestMapping(value = "excel2", method = RequestMethod.POST)
    public AjaxResult excel2(@RequestParam(value = "file") MultipartFile file) {
        // 获取文件上传流
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (Exception e) {
            return AjaxResult.error("导入失败");
        }

        ExcelReader excelReader = ExcelUtil.getReader(inputStream, 0);
        if (excelReader.getRowCount() <= 1) {
            return AjaxResult.error("文件没有数据");
        }

        Integer success = 0;
        Integer error = 0;

        List<List<Object>> read = excelReader.read(1, excelReader.getRowCount());
        for (List<Object> objects : read) {
            if (ObjectUtil.length(objects) == 0) {
                continue;
            }

            boolean exits = materialService.existMaterial(objects.get(1).toString());
            if (exits == true) {
                continue;
            }

            if (StrUtil.isEmpty(objects.get(3).toString())) {
                continue;
            }

            String midNameEng = objects.get(2).toString();
            String packages = "";

            if (midNameEng.contains("209L")) {
                packages = "209L";
            }

            if (midNameEng.contains("20L")) {
                packages = "20L";
            }

            if (midNameEng.contains("18L")) {
                packages = "18L";
            }

            if (midNameEng.contains("4L")) {
                packages = "4L";
            }

            String skuNo = objects.get(1).toString();
            String skuDesc = objects.get(3).toString().replace("`", "");

            MaterialForm materialForm = new MaterialForm();
            materialForm.setSkuNo(skuNo);
            materialForm.setSkuDesc(skuDesc);
            materialForm.setSkuSpec(packages);
            materialForm.setPallet2carton(39);
            materialForm.setCartonUnit("桶");

            materialForm.setMidNameEng(midNameEng);
            materialForm.setMidNameChn(skuDesc);
            materialForm.setFamily("");
            materialForm.setProductName("");
            materialForm.setProductGroup("");
            materialForm.setSubGroup("");
            materialForm.setPackages(packages);
            materialForm.setLabellingType("");
            materialForm.setSourceType("码中台");

            Boolean result = materialService.insertMaterial(materialForm);
            if (result) {
                success++;
            } else {
                error++;
            }
        }

        return AjaxResult.success("导入成功" + success + "条，导入失败：" + error + "条");
    }


    /**
     * 下载模板
     *
     * @param response
     * @param request
     */
    @Log(title = "物料管理", businessType = BusinessType.IMPORT)
    @ResponseBody
    @RequestMapping(value = "download", method = RequestMethod.POST)
    public void download(HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            ClassPathResource cpr = new ClassPathResource("/model.xlsx");
            InputStream is = null;
            try {
                is = cpr.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Workbook workbook = new XSSFWorkbook(is);
            String fileName = "model.xlsx";
            excelService.downLoadExcel(fileName, response, workbook);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
