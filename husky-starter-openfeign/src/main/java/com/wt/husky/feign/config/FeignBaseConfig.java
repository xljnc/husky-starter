package com.wt.husky.feign.config;

import com.wt.husky.feign.annotation.EnableHuskyFeignClients;
import feign.Feign;
import io.undertow.Undertow;
import okhttp3.ConnectionPool;
import okhttp3.Protocol;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author 一贫
 * @date 2021/1/4
 */
@EnableFeignClients(basePackages = "${feign.basePackages:com.wt.**.feign}", defaultConfiguration = FeignMvcConfig.class)
@EnableHuskyFeignClients(basePackages = "${feign.basePackages:com.wt.**.feign}", defaultConfiguration = FeignMvcConfig.class)
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Feign.class)
@ConditionalOnProperty(value = "feign.okhttp.enabled", havingValue = "true", matchIfMissing = true)
@ConfigurationProperties(prefix = "feign.httpclient")
@EnableDiscoveryClient
public class FeignBaseConfig {

    private okhttp3.OkHttpClient okHttpClient;

    private Long readTimeout = 60000L;

    private Long writeTimeout = 120000L;

    @Bean
    @ConditionalOnMissingBean(ConnectionPool.class)
    public ConnectionPool httpClientConnectionPool(FeignHttpClientProperties httpClientProperties,
                                                   OkHttpClientConnectionPoolFactory connectionPoolFactory) {
        Integer maxTotalConnections = httpClientProperties.getMaxConnections();
        Long timeToLive = httpClientProperties.getTimeToLive();
        TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
        return connectionPoolFactory.create(maxTotalConnections, timeToLive, ttlUnit);
    }

    @Bean
    @ConditionalOnClass(Undertow.class)
    public okhttp3.OkHttpClient client(OkHttpClientFactory httpClientFactory, ConnectionPool connectionPool,
                                       FeignHttpClientProperties httpClientProperties) {
        this.okHttpClient = httpClientFactory.createBuilder(httpClientProperties.isDisableSslValidation())
                .connectTimeout(httpClientProperties.getConnectionTimeout(), TimeUnit.MILLISECONDS).connectionPool(connectionPool)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS).writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .followRedirects(httpClientProperties.isFollowRedirects())
                .protocols(Arrays.asList(Protocol.H2_PRIOR_KNOWLEDGE, Protocol.HTTP_1_1))
                .build();
        return this.okHttpClient;
    }

    @Bean
    @ConditionalOnClass(Tomcat.class)
    @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
    public okhttp3.OkHttpClient tomcatClient(OkHttpClientFactory httpClientFactory, ConnectionPool connectionPool,
                                       FeignHttpClientProperties httpClientProperties) {
        this.okHttpClient = httpClientFactory.createBuilder(httpClientProperties.isDisableSslValidation())
                .connectTimeout(httpClientProperties.getConnectionTimeout(), TimeUnit.MILLISECONDS).connectionPool(connectionPool)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS).writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .followRedirects(httpClientProperties.isFollowRedirects())
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();
        return this.okHttpClient;
    }

    @PreDestroy
    public void destroy() {
        if (this.okHttpClient != null) {
            this.okHttpClient.dispatcher().executorService().shutdown();
            this.okHttpClient.connectionPool().evictAll();
        }
    }
}
