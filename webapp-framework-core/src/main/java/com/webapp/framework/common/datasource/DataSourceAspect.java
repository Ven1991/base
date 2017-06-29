package com.webapp.framework.common.datasource;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceAspect {  
    //log   
    private static final Logger LOG = LoggerFactory.getLogger(DataSourceAspect.class);  
  
    public void before(JoinPoint point) {  
        Object target = point.getTarget();  
        String method = point.getSignature().getName();  
        Class<?>[] classz = target.getClass().getInterfaces();  
        Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();  
        try {  
            Method methodRefRes = classz[0].getMethod(method, parameterTypes);  
            if (methodRefRes != null && methodRefRes.isAnnotationPresent(DataSource.class)) {  
                DataSource data = methodRefRes.getAnnotation(DataSource.class);  
                DynamicDataSourceHolder.putDataSource(data.value());  
                LOG.info("\n************************************************\n" + "\t~~~DB:: " + data.value() + "\n************************************************");  
            }  
        } catch (Exception e) {  
            LOG.error("数据源失败切面获取异常:" + e.getMessage(), e);  
        }  
    }
    
    
    
    
}  