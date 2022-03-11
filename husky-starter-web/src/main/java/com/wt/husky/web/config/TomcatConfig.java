package com.wt.husky.web.config;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author qiyu
 * @date 2022/1/30
 */
@Configuration
@ConditionalOnClass(Tomcat.class)
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>, EnvironmentAware {

    @Value("${server.port:443}")
    private int port;

    @Value("${http.port:80}")
    private int httpPort;

    private Environment environment;

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setSecure(false);
        connector.addUpgradeProtocol(new Http2Protocol());
        connector.setPort(httpPort);
        if (Boolean.valueOf(environment.getProperty("server.ssl.enabled", "false"))) {
            connector.setRedirectPort(port);
            factory.addContextCustomizers(context -> {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            });
        }
        factory.addAdditionalTomcatConnectors(connector);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
