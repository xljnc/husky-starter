package com.wt.husky.feign.config;

import feign.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 一贫
 * @date 2021/1/4
 */
@Configuration
public class FeignMvcConfig {

    @Autowired(required = false)
    private FeignClientProperties feignClientProperties;

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

    @Bean
    @Qualifier("mvcContract")
    @Primary
    public Contract feignSpringMvcContract(ConversionService feignConversionService) {
        boolean decodeSlash = feignClientProperties == null || feignClientProperties.isDecodeSlash();
        return new SpringMvcContract(parameterProcessors, feignConversionService, decodeSlash);
    }
}
