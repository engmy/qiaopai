package com.qixuan.api.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.qixuan.api.annotation.Log;
import com.qixuan.api.service.ApiLogService;
import com.qixuan.common.entity.ApiLog;
import com.qixuan.common.enums.BusinessStatus;
import com.qixuan.common.utils.AjaxResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 接口操作日志记录处理
 *
 * @author huxg
 */
@Aspect
@Component
public class ApiLogAspect
{
    private static final Logger log = LoggerFactory.getLogger(ApiLogAspect.class);

    @Resource
    private HttpServletRequest request;

    @Resource
    private ApiLogService apiLogService;

    // 配置织入点
    @Pointcut("@annotation(com.qixuan.api.annotation.Log)")
    public void apiLogPoint() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "apiLogPoint()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult)
    {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "apiLogPoint()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e)
    {
        handleLog(joinPoint, e, AjaxResult.error(e.getMessage()));
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception exception, Object jsonResult)
    {
        try
        {
            // 获得注解
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null)
            {
                return;
            }

            ApiLog apiLog = new ApiLog();
            apiLog.setStartTime(System.currentTimeMillis());
            apiLog.setStatus(BusinessStatus.SUCCESS.getCode());

            // 请求的地址
            apiLog.setIp(ServletUtil.getClientIP(request));
            String urlStr = request.getRequestURL().toString();
            apiLog.setBasePath(StrUtil.removeSuffix(urlStr, URLUtil.url(urlStr).getPath()));

            // 返回参数
            apiLog.setResponse(JSONUtil.toJsonStr(jsonResult));
            apiLog.setUri(request.getRequestURI());

            // 异常处理
            if (exception != null)
            {
                apiLog.setStatus(BusinessStatus.FAIL.getCode());
                apiLog.setErrorMsg(exception.getMessage());
            }

            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            apiLog.setAction(className + "." + methodName + "()");

            // 设置请求方式
            apiLog.setMethod(request.getMethod());

            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, apiLog);

            // 保存数据库
            apiLogService.insertApiLog(apiLog);
        }
        catch (Exception exp)
        {
            log.error("操作记录异常:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log 日志
     * @param apiLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, ApiLog apiLog) throws Exception
    {
        // 设置标题
        apiLog.setTitle(log.title());

        // 获取所有参数
        if (log.isSaveRequestData())
        {
            String params = getRequestParams(joinPoint, apiLog.getMethod());
            apiLog.setParams(params);
        }
    }

    /**
     * 获取请求的参数，放到log中
     * @param joinPoint
     * @param requestMethod
     * @return
     */
    private String getRequestParams(JoinPoint joinPoint, String requestMethod) throws Exception
    {
        if (HttpMethod.POST.name().equals(requestMethod))
        {
            String params = argsArrayToString(joinPoint.getArgs());
            return params;
        }else if(HttpMethod.PUT.name().equals(requestMethod))
        {
            Map paramsMap = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            Map urlParamMap = ServletUtil.getParamMap(request);
            urlParamMap.putAll(paramsMap);
            return urlParamMap.toString();
        }
        else if((HttpMethod.GET.name().equals(requestMethod)))
        {
            Map paramsMap = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            Map urlParamMap = ServletUtil.getParamMap(request);
            urlParamMap.putAll(paramsMap);
            return urlParamMap.toString();
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String params = paramsMap.toString();
            return params;
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception
    {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null)
        {
            return method.getAnnotation(Log.class);
        }
        return null;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray)
    {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0)
        {
            for (int i = 0; i < paramsArray.length; i++)
            {
                Object paramObj = paramsArray[i];
                if (ObjectUtil.isNotNull(paramObj) && !isFilterObject(paramObj))
                {
                    String paramStr = JSONUtil.toJsonStr(paramObj);
                    params += paramStr + " ";
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o)
    {
        Class<?> clazz = o.getClass();
        if (clazz.isArray())
        {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        }
        else if (Collection.class.isAssignableFrom(clazz))
        {
            Collection collection = (Collection) o;
            for (Iterator iter = collection.iterator(); iter.hasNext();)
            {
                return iter.next() instanceof MultipartFile;
            }
        }
        else if (Map.class.isAssignableFrom(clazz))
        {
            Map map = (Map) o;
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();)
            {
                Map.Entry entry = (Map.Entry) iter.next();
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
