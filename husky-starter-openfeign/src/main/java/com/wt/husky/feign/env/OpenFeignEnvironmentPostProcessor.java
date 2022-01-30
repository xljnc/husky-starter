package com.wt.husky.feign.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.StringUtils;

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
        //开启http2
        feignProperties.put("server.http2.enabled", Boolean.TRUE);
        feignProperties.put("server.compression.enabled", Boolean.TRUE);
        //开启https
        feignProperties.put("server.ssl.key-store", "classpath:keystore.p12");
        feignProperties.put("server.ssl.key-password", "123456");
        feignProperties.put("server.ssl.key-store-password", "123456");
        feignProperties.put("server.ssl.enabled", Boolean.TRUE);
        feignProperties.put("server.ssl.protocol", "TLS");
        //启用spring bean定义重写
        feignProperties.put("spring.main.allow-bean-definition-overriding", Boolean.TRUE);
        if (environment.containsProperty("spring.application.name")) {
            String contextPath = environment.getProperty("spring.application.name");
            if (StringUtils.hasText(contextPath)) {
                if (!contextPath.startsWith("/"))
                    contextPath = "/" + contextPath;
                feignProperties.put("server.servlet.context-path", contextPath);
            }
        }
        //扫描路径
//        String[] basePackages = new String[]{"com.wt.**.feign"};
//        feignProperties.put("feign.basePackages", basePackages);
        PropertiesPropertySource propertySource = new PropertiesPropertySource("feign", feignProperties);
        environment.getPropertySources().addFirst(propertySource);
    }

}
