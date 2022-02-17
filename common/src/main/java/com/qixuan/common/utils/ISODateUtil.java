package com.qixuan.common.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISODateUtil
{
    public static Date dateToISODate(Date dateStr)
    {
        Date parse = null;
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            parse = format.parse(format.format(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    public static Date dateToISODate(String dateStr)
    {
        DateTime dateTime = DateUtil.parse(dateStr, "yyyy-MM-dd");
        return dateToISODate(dateTime);
    }
}
