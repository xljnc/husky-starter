package com.wt.husky.feign.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

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
        String remoteConfigEnabled = environment.getProperty("config.center.enable");

    }
}
