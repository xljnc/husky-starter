package com.wt.husky.config.center.env;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.wt.husky.config.center.util.YamlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * 从nacos中获取配置
 *
 * @author qiyu
 * @date 2022/1/15
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(value = "config.center.enable", havingValue = "true", matchIfMissing = true)
@Slf4j
public class NacosEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final String CONFIG_DEFAULT_GROUP = "DEFAULT_GROUP";
    private final String CONFIG_DEFAULT_NAMESPACE = "public";
    private final String CONFIG_DEFAULT_USERNAME = "nacos";
    private final String CONFIG_DEFAULT_PASSWORD = "nacos";
    private final String CONFIG_DEFAULT_EXT = "yaml";


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String serverAddr = environment.getProperty("config.center.server.addr");
        if (!StringUtils.hasText(serverAddr))
            throw new RuntimeException("请指定正确的配置中心地址!");
        String namespace = environment.getProperty("config.center.namespace");
        if (!StringUtils.hasText(namespace))
            namespace = CONFIG_DEFAULT_NAMESPACE;
        String username = environment.getProperty("config.center.username");
        if (!StringUtils.hasText(username))
            username = CONFIG_DEFAULT_USERNAME;
        String password = environment.getProperty("config.center.password");
        if (!StringUtils.hasText(password))
            password = CONFIG_DEFAULT_PASSWORD;
        Properties configCenterProperties = new Properties();
        configCenterProperties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        configCenterProperties.put(PropertyKeyConst.NAMESPACE, namespace);
        configCenterProperties.put(PropertyKeyConst.USERNAME, username);
        configCenterProperties.put(PropertyKeyConst.PASSWORD, password);
        ConfigService configService = null;
        try {
            configService = NacosFactory.createConfigService(configCenterProperties);
        } catch (NacosException e) {
            log.error("连接配置中心失败,地址:{}", serverAddr, e);
            throw new RuntimeException("连接配置中心失败");
        }
        String dataId = environment.getProperty("config.center.dataId");
        if (!StringUtils.hasText(dataId)) {
            dataId = environment.getProperty("spring.application.name");
            if (!StringUtils.hasText(dataId))
                throw new RuntimeException("请指定正确的dataId!");
        }
        String group = environment.getProperty("config.center.group");
        if (!StringUtils.hasText(group))
            group = CONFIG_DEFAULT_GROUP;
        String content = null;
        try {
            content = configService.getConfig(dataId, group, 5000);
        } catch (NacosException e) {
            log.error("连接配置失败,dataId:{},group:{}", dataId, group, e);
            throw new RuntimeException("连接配置失败");
        }
        if (StringUtils.hasText(content)) {
            String ext = environment.getProperty("config.center.ext");
            if (!StringUtils.hasText(ext))
                ext = CONFIG_DEFAULT_EXT;
            Properties config = null;
            if (ext.equals(CONFIG_DEFAULT_EXT)) {
                config = YamlUtil.loadToProperties(content);
            } else {
                config = new Properties();
                try {
                    config.load(new StringReader(content));
                    log.info("读取配置内容:{}", content);
                } catch (IOException e) {
                    log.error("配置有误，请检查。内容:{}", content, e);
                    throw new RuntimeException("连接配置失败");
                }
            }
            PropertiesPropertySource propertySource = new PropertiesPropertySource("config-center", config);
            environment.getPropertySources().addFirst(propertySource);
        }

    }
}
