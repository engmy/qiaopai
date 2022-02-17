package com.qixuan.common.exception;

import cn.hutool.http.HttpStatus;
import com.qixuan.common.service.EmailService;
import com.qixuan.common.utils.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException
{
    @Autowired
    private EmailService emailService;

    private Logger logger = LoggerFactory.getLogger(GlobalException.class);

    /**
     * 全局捕获异常
     * @param exception
     * @return
     * @throws IOException
     */
    @ExceptionHandler(value = Exception.class)
    public AjaxResult globalException(Exception exception) throws IOException
    {
        if (exception instanceof NoHandlerFoundException)
        {
            return AjaxResult.error(HttpStatus.HTTP_NOT_FOUND, "请检查请求路径或者类型是否正确");
        } else if(exception instanceof CustomException)
        {
            return AjaxResult.error(HttpStatus.HTTP_BAD_REQUEST, exception.getMessage());
        }else if(exception instanceof ConstraintViolationException)
        {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) exception).getConstraintViolations();
            String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
            return AjaxResult.error(HttpStatus.HTTP_BAD_REQUEST, message);
        }else {
            if(exception.getMessage()==null)
            {
                logger.error(exception.toString());
                emailService.pushEmail("服务器内部错误，请尽快处理", exception.toString());
                return AjaxResult.error(HttpStatus.HTTP_INTERNAL_ERROR, exception.toString());
            }else{
                logger.error(exception.getMessage());
                emailService.pushEmail("服务器内部错误，请尽快处理", exception.getMessage());
                return AjaxResult.error(HttpStatus.HTTP_INTERNAL_ERROR, exception.getMessage());
            }
        }
    }

    /**
     * 捕获错误验证
     * @param exception
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public AjaxResult validateErrorHandler(BindException exception)
    {
        BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.hasErrors())
        {
            List<FieldError> errorList = bindingResult.getFieldErrors();
            return AjaxResult.error(errorList.get(0).getDefaultMessage());
        } else
        {
            return AjaxResult.error("未知错误");
        }
    }
}
