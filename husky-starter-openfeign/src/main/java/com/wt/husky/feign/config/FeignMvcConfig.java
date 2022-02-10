package com.wt.husky.feign.config;

import feign.Contract;
import feign.MethodMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.CollectionFormat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

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
        return new HuskySpringMvcContract(parameterProcessors, feignConversionService, decodeSlash);
    }

    @Bean
    public WebMvcRegistrations feignWebRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new FeignRequestMappingHandlerMapping();
            }
        };
    }

    @Slf4j
    static class HuskySpringMvcContract extends SpringMvcContract {

        HuskySpringMvcContract() {
            super();
        }

        HuskySpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors,
                               ConversionService conversionService, boolean decodeSlash) {
            super(annotatedParameterProcessors, conversionService, decodeSlash);
        }

        @Override
        protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz) {
            RequestMapping classAnnotation = findMergedAnnotation(clz, RequestMapping.class);
            //这里区别原有的SpringMvcContract，SpringMvcContract不允许@FeignClient接口上存在@RequestMapping注解
            if (classAnnotation != null)
                log.info("process class: " + clz.getName()
                        + ". with @RequestMapping annotation on @FeignClient interfaces.");
            CollectionFormat collectionFormat = findMergedAnnotation(clz, CollectionFormat.class);
            if (collectionFormat != null) {
                data.template().collectionFormat(collectionFormat.value());
            }
        }
    }

    static class FeignRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected boolean isHandler(Class<?> beanType) {
            return super.isHandler(beanType) && !AnnotatedElementUtils.hasAnnotation(beanType, FeignClient.class);
        }
    }
}
