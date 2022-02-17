package com.qixuan.admin.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class AdminException
{
    /**
     * 捕获无权限异常
     * @param request
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(value = NotPermissionException.class)
    public void notPermissionException(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.sendRedirect(request.getContextPath()+"/404.html");
    }

    /**
     * 登录异常
     * @param request
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(value = NotLoginException.class)
    public void notLoginException(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.sendRedirect(request.getContextPath()+"/admin/login");
    }
}
