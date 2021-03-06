package com.qixuan.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware
{
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        if(SpringUtil.applicationContext == null)
        {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 获取applicationContext
     * @return
     */
    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     *  通过 name 获取Bean
     * @return
     */
    public  static Object getBean(String name)
    {
        Class cla = null;
        try {
            cla = Class.forName(name);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return getApplicationContext().getBean(cla);
    }

    /**
     * 通过class获取Bean.
     * @return
     */
    public  static <T> T getBean(Class<T> clazz)
    {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean.
     * @return
     */
    public  static <T> T getBean(String name,Class<T> clazz)
    {
        return getApplicationContext().getBean(name, clazz);
    }
}

