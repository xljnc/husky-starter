package com.wt.husky.web.config;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author qiyu
 * @date 2022/1/30
 */
@Configuration
@ConditionalOnClass(Undertow.class)
@ConditionalOnExpression
public class UndertowConfig {

    @Bean
    public UndertowServletWebServerFactory undertowServletWebServerFactory(ConfigurableEnvironment environment) {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        if (Boolean.valueOf(environment.getProperty("server.http2.enabled", "false")))
            factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
        if (Boolean.valueOf(environment.getProperty("server.ssl.enabled", "false")))
            factory.addBuilderCustomizers(builder -> builder.addHttpListener(
                    Integer.valueOf(environment.getProperty("http.port", "80")), "0.0.0.0"));
        return factory;
    }

}
