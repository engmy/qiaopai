package com.qixuan.common.utils;

import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.util.ParsingUtils;

import java.util.Iterator;
import java.util.List;

public class CamelCase implements FieldNamingStrategy
{
    /***
     * 驼峰转下划线
     */
    @Override
    public String getFieldName(PersistentProperty<?> property)
    {
        List<String> parts = ParsingUtils.splitCamelCaseToLower(property.getName());
        StringBuilder sb = new StringBuilder();
        Iterator it = parts.iterator();
        if(it.hasNext())
        {
            sb.append(it.next().toString());
            while (it.hasNext()){
                sb.append("_");
                sb.append(it.next().toString());
            }
        }
        return sb.toString();
    }

    /***
     * 下划线命名转驼峰命名
     */
    public String underlineToHump(String source)
    {
        StringBuffer result = new StringBuffer();
        String[] params = source.split("_");
        for (String param : params)
        {
            if (result.length() == 0) {
                result.append(param.toLowerCase());
            } else {
                result.append(param.substring(0, 1).toUpperCase());
                result.append(param.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}