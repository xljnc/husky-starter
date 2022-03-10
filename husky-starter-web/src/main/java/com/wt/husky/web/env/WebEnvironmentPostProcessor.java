package com.wt.husky.web.env;

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
@Order(Ordered.LOWEST_PRECEDENCE - 200)
public class WebEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties webProperties = new Properties();
        //默认开启http2
        if (!environment.containsProperty("server.http2.enabled"))
            webProperties.put("server.http2.enabled", Boolean.TRUE);
        webProperties.put("server.compression.enabled", Boolean.TRUE);
        //如果开启https而且没有详细配置
        if (Boolean.valueOf(environment.getProperty("server.ssl.enabled", "false")) && !environment.containsProperty("server.ssl.key-store")) {
            webProperties.put("server.ssl.key-store", "classpath:keystore.p12");
            webProperties.put("server.ssl.key-password", "123456");
            webProperties.put("server.ssl.key-store-password", "123456");
            webProperties.put("server.ssl.protocol", "TLS");
        }
        //使用spring.application.name作为默认的context-path
        if (!environment.containsProperty("server.servlet.context-path") && environment.containsProperty("spring.application.name")) {
            String contextPath = environment.getProperty("spring.application.name");
            if (StringUtils.hasText(contextPath)) {
                if (!contextPath.startsWith("/"))
                    contextPath = "/" + contextPath;
                webProperties.put("server.servlet.context-path", contextPath);
            }
        }
        PropertiesPropertySource propertySource = new PropertiesPropertySource("web", webProperties);
        environment.getPropertySources().addFirst(propertySource);
    }

}
