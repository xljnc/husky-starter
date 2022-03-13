package com.wt.husky.feign.config;

import com.wt.husky.feign.fallback.FeignHttpFallback;
import feign.Contract;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 一贫
 * @date 2021/1/4
 */
@Configuration
public class FeignHttpConfig {

    @Bean
    @Qualifier("httpContract")
    public Contract feignHttpContract() {
        return new feign.Contract.Default();
    }


    @Bean
    public FeignHttpFallback feignHttpFallback() {
        return new FeignHttpFallback();
    }
}
