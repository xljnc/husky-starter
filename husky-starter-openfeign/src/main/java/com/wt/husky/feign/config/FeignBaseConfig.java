package com.wt.husky.feign.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author 一贫
 * @date 2021/1/4
 */
@EnableFeignClients("${feign.basePackages}")
@Import(FeignClientsConfiguration.class)
@Configuration
public class FeignBaseConfig {


}
