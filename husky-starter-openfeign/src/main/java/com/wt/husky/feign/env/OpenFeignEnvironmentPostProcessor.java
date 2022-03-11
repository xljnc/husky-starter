package com.wt.husky.feign.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * 从Nacos中获取配置
 *
 * @author qiyu
 * @date 2022/1/15
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100)
@Slf4j
public class OpenFeignEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties feignProperties = new Properties();
        //开启okhttp，禁用httpclient
        feignProperties.put("feign.okhttp.enabled", Boolean.TRUE);
        feignProperties.put("feign.httpclient.enabled", Boolean.FALSE);
        //开启压缩
        feignProperties.put("feign.compression.request.enabled", Boolean.TRUE);
        feignProperties.put("feign.compression.response.enabled", Boolean.TRUE);
        //默认开启http2
        if (!environment.containsProperty("feign.http2.enabled"))
            feignProperties.put("feign.http2.enabled", Boolean.TRUE);
        //默认关闭https
        if (!environment.containsProperty("feign.ssl.enabled"))
            feignProperties.put("feign.ssl.enabled", Boolean.FALSE);
        //启用spring bean定义重写
        feignProperties.put("spring.main.allow-bean-definition-overriding", Boolean.TRUE);
        //扫描路径
//        String[] basePackages = new String[]{"com.wt.**.feign"};
//        feignProperties.put("feign.basePackages", basePackages);
        PropertiesPropertySource propertySource = new PropertiesPropertySource("feign", feignProperties);
        environment.getPropertySources().addFirst(propertySource);
    }

}
