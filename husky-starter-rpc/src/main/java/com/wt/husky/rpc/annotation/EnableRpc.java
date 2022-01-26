package com.wt.husky.rpc.annotation;

import com.wt.husky.rpc.config.RpcProviderRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiyu
 * @date 2022/1/16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(RpcProviderRegistrar.class)
public @interface EnableRpc {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};

}
