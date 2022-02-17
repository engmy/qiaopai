package com.qixuan.admin.aspect;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qixuan.admin.service.LoginLogService;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Log4j2
@Aspect
@Component
public class LoginLogAspect
{
    @Resource
    private HttpServletRequest request;

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 配置切入点
     */
    @Pointcut("execution(public * com.qixuan.admin.controller.LoginController.login(..))")
    public void loginLog() {
    }

    /**
     * 前置方法,在目标方法执行前执行
     *
     * @param joinPoint
     */
    @Before("loginLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable
    {
    }

    /**
     * 环绕通知
     *
     * @param joinPoint
     */
    @Around("loginLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Object result = joinPoint.proceed();
        Map<String, String> paramMap = ServletUtil.getParamMap(request);

        // 密码托敏
        paramMap.put("password", DesensitizedUtil.password(paramMap.get("password")));

        // 获取请求参数
        String params = JSONUtil.toJsonStr(paramMap);

        // 获取用户名
        String username = paramMap.get("username");

        // 获取相应参数
        String response = JSONUtil.toJsonStr(result);

        // 获取状态码
        JSONObject jsonObject = new JSONObject(result);
        String code = jsonObject.getStr("code");
        Integer status = code.equals("200") ? 1 : 2;

        loginLogService.insertLoginLog(username, status, params, response);
        return result;
    }

    /**
     * 后置方法，与@Before相反，在目标方法执行完毕后执行
     *
     * @param joinPoint
     */
    @After("loginLog()")
    public void after(JoinPoint joinPoint) {
    }

    /**
     * 后置通知，在将返回值返回时执行
     *
     * @param result
     */
    @AfterReturning(value = "loginLog()", returning = "result")
    public void doAfterReturning(Object result) throws Throwable {
    }

    /**
     * 后置异常通知
     * @param joinPoint
     * @param exception
     * @throws Throwable
     */
    @AfterThrowing(value = "loginLog()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) throws Throwable {
    }
}