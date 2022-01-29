package com.wt.husky.util.copier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.context.annotation.ComponentScan;

import java.lang.reflect.InvocationTargetException;

/**
 * @author qiyu
 * @date 2022/1/29
 */
@ConditionalOnClass(BeanCopier.class)
@ComponentScan
@Slf4j
public class BeanUtils {

    public void copy(Object source, Object target) {
        BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
        beanCopier.copy(source, target, null);
    }

    public <T> T copy(Object source, Class<T> targetClass) throws RuntimeException {
        BeanCopier beanCopier = BeanCopier.create(source.getClass(), targetClass, false);
        T target = null;
        try {
            target = targetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            String msg = String.format("目标类%s没有默认构造函数", targetClass.getName());
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        beanCopier.copy(source, target, null);
        return target;
    }
}
