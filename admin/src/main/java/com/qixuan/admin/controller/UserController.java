package com.qixuan.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.qixuan.admin.annotation.Log;
import com.qixuan.admin.form.AdminForm;
import com.qixuan.admin.form.EditPassForm;
import com.qixuan.admin.form.ProfileForm;
import com.qixuan.admin.form.ValidGroups;
import com.qixuan.admin.service.AdminService;
import com.qixuan.admin.service.ConfigService;
import com.qixuan.admin.service.RoleService;
import com.qixuan.common.entity.Admin;
import com.qixuan.common.enums.BusinessType;
import com.qixuan.common.utils.AjaxResult;
import com.qixuan.common.utils.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/user")
public class UserController
{
    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ConfigService configService;

    @Value("${custom.upload.user-avatar-file-path}")
    private String userAvatarFilePath;

    @Autowired
    private HttpSession session;

    /**
     * 用户管理
     */
    @SaCheckPermission("admin:user:index")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index(@RequestParam Map params, ModelMap search)
    {
        search.put("params",   params);
        search.put("roleList", roleService.getRoleList());
        return "admin/user/index";
    }

    /**
     * 用户列表
     */
    @ResponseBody
    @SaCheckPermission("admin:user:index")
    @RequestMapping(value = "data", method = RequestMethod.GET, headers = "Accept=application/json")
    public AjaxResult data(@RequestParam(value="page", defaultValue="1") Integer page,
    @RequestParam(value="limit", defaultValue="10") Integer limit, @RequestParam Map params)
    {
        PageHelper list = adminService.getUserList(page, limit, params);
        return AjaxResult.success(list);
    }

    /**
     * 新增用户
     */
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(ModelMap map)
    {
        map.put("roleList", roleService.getRoleListNeSys());
        return "admin/user/add";
    }

    /**
     * 编辑用户
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(@RequestParam(value="id") String id, ModelMap map)
    {
        if(StrUtil.isNotEmpty(id))
        {
            map.put("info", adminService.getUserInfo(id));
        }
        map.put("roleList", roleService.getRoleListNeSys());
        return "admin/user/edit";
    }

    /**
     * 新增用户
     */
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult insert(@Validated(ValidGroups.Insert.class) AdminForm adminForm)
    {
        if(adminService.existUser(adminForm.getUsername()))
        {
            return AjaxResult.error("用户名已存在!");
        }
        if(adminService.existPhone((adminForm.getPhone())))
        {
            return AjaxResult.error("手机号已被占用！");
        }
        if(adminService.existEmail(adminForm.getEmail()))
        {
            return  AjaxResult.error("邮箱已被占用！");
        }
        Boolean result = adminService.insertUser(adminForm);
        if(result) {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 更新用户
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult update(@Validated(ValidGroups.Update.class) AdminForm adminForm)
    {
        Boolean result = adminService.updateUser(adminForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 删除用户
     */
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult delete(@RequestParam(value="id") String userId)
    {
        Boolean result = adminService.deleteUser(userId);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 修改密码 Map params
     */
    @RequestMapping(value = "editpass", method = RequestMethod.GET)
    public String pass(@RequestParam Map params, ModelMap search)
    {
        search.put("params", params);
        return "admin/user/editpass";
    }

    /**
     * 修改密码
     */
    @Log(title = "修改密码", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "editpass", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult editPass(@Validated EditPassForm editPassForm)
    {
        Boolean result = adminService.editPassWord(editPassForm);
        if(result)
        {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    /**
     * 个人资料
     */
    @RequestMapping(value = "profile", method = RequestMethod.GET)
    public String profile(HttpSession session, ModelMap map)
    {
        // 用户信息
        Admin admin = (Admin) session.getAttribute("admin");
        map.put("user", adminService.getUserInfo(admin.getUserId()));

        // 角色信息
        map.put("roleList", roleService.getRoleList());

        // 工厂名称
        map.put("siteName", configService.getConfigByKey("site_name"));

        return "admin/user/profile";
    }

    /**
     * 更新用户资料
     */
    @Log(title = "基本资料", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "profile", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult update(@Validated ProfileForm profileForm)
    {
        Boolean result = adminService.updateProfile(profileForm);
        if(result) {
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }

    @Log(title = "状态更新", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "change",method = RequestMethod.POST,headers = "Accept=application/json")
    public AjaxResult change(@RequestParam(value = "id") String id, @RequestParam(value = "status") Integer status)
    {
        Boolean result = adminService.updateStatus(id,status);
        if(result) {
            return AjaxResult.success("更新状态成功！");
        } else {
            return AjaxResult.error("更新状态失败！");
        }
    }

    @Log(title = "头像修改", businessType = BusinessType.UPDATE)
    @ResponseBody
    @RequestMapping(value = "avatar", method = RequestMethod.POST, headers = "Accept=application/json")
    public AjaxResult updateUserAvatar(@RequestParam("file") MultipartFile file)
    {
        // 判断文件是否为空
        if(file.isEmpty()) {
            return AjaxResult.error("上传文件为空");
        }
        // 判断是否是图片格式
        if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
            return AjaxResult.error("上传的文件不是jpg或png格式");
        }
        // 开始上传文件的操作
        Boolean flage = adminService.updateAvatar(this.userAvatarFilePath, file);
        if(!flage) {
            return AjaxResult.error("文件上传失败");
        }else {
            return AjaxResult.success("文件上传成功");
        }
    }

    @RequestMapping( value = "avatar", method = RequestMethod.GET, headers = "Accept=application/json")
    public void getUserAvatar(HttpServletResponse response) throws IOException
    {
        Admin admin = (Admin) session.getAttribute("admin");
        Admin result = adminService.getUserInfo(admin.getUserId());

        ServletOutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            String imgPath = result.getAvatar();
            if(StrUtil.isEmpty(imgPath))
            {
                ClassPathResource classPathResource = new ClassPathResource("/static/admin/img/logo.png");
                inputStream = classPathResource.getInputStream();
            }else{
                inputStream = FileUtil.getInputStream(imgPath);
            }
            response.setContentType("image/png");
            outputStream = response.getOutputStream();

            int len = 0;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }
}
