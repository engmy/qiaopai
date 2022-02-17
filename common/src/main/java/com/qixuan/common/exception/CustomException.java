package com.qixuan.common.exception;

/**
 * 自定义异常
 *
 * @author huxg
 */
public class CustomException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String msg;

    public CustomException(String msg)
    {
        this.msg = msg;
    }

    public CustomException(String msg, Integer code)
    {
        this.msg = msg;
        this.code = code;
    }

    public CustomException(String msg, Throwable e)
    {
        super(msg, e);
        this.msg = msg;
    }

    @Override
    public String getMessage()
    {
        return msg;
    }

    public Integer getCode()
    {
        return code;
    }
}
