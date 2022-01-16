package com.wt.husky.config.center.util;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author qiyu
 * @date 2022/1/16
 */
public class YamlUtil {

    public static Properties loadToProperties(String content) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new InputStreamResource(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))));
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
