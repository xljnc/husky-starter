package com.wt.husky.web.config;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author qiyu
 * @date 2022/1/30
 */
@Configuration
@ConditionalOnClass(Undertow.class)
public class UndertowConfig implements WebServerFactoryCustomizer<UndertowServletWebServerFactory>, EnvironmentAware {

    private Environment environment;

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        if (Boolean.valueOf(environment.getProperty("server.http2.enabled", "false")))
            factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
        if (Boolean.valueOf(environment.getProperty("server.ssl.enabled", "false")))
            factory.addBuilderCustomizers(builder -> builder.addHttpListener(
                    Integer.valueOf(environment.getProperty("http.port", "80")), "0.0.0.0"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
