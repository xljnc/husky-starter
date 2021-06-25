package com.wt.husky.rocketmq.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 一贫
 * @date 2021/1/4
 */
@Component
@ConditionalOnMissingBean(RocketMqUtil.class)
public class RocketMqUtil {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 同步发送
     *
     * @param topic   主题
     * @param payload 消息体
     * @return void
     */
    public void send(String topic, Object payload) {
        send(topic, payload, null, null);
    }

    /**
     * 同步发送,包含消息头
     *
     * @param topic   主题
     * @param payload 消息体
     * @param tag     消息标签,可以为空
     * @param headers 消息头,可以为空
     * @return void
     */
    public void send(String topic, Object payload, String tag, Map<String, Object> headers) {
        if (StringUtils.isNotBlank(tag))
            topic = new StringBuilder(topic).append(":").append(tag).toString();
        rocketMQTemplate.convertAndSend(topic, payload, headers);
    }

    /**
     * 同步顺序发送
     *
     * @param topic   主题
     * @param payload 消息体
     * @param hashKey 队列选择的hash
     * @return void
     */
    public void sendOrderly(String topic, Object payload, String hashKey) {
        sendOrderly(topic, payload, hashKey, null, null);
    }

    /**
     * 同步顺序发送,包含消息头
     *
     * @param topic   主题
     * @param payload 消息体
     * @param hashKey 队列选择的hash
     * @param tag     消息标签,可以为空
     * @param headers 消息头
     * @return void
     */
    public void sendOrderly(String topic, Object payload, String hashKey, String tag, Map<String, Object> headers) {
        MessageBuilder<?> builder = MessageBuilder.withPayload(payload);
        if (!headers.isEmpty()) {
            headers.forEach((k, v) -> {
                builder.setHeader(k, v);
            });
        }
        Message<?> message = builder.build();
        if (StringUtils.isNotBlank(tag))
            topic = new StringBuilder(topic).append(":").append(tag).toString();
        rocketMQTemplate.syncSendOrderly(topic, message, hashKey);
    }

    /**
     * 同步延迟发送
     *
     * @param topic      主题
     * @param payload    消息体
     * @param delayLevel 延迟级别,0代表不延迟，2代表延迟5秒
     * @return void
     */
    public void sendWithDelay(String topic, Object payload, int delayLevel) {
        sendWithDelay(topic, payload, null, null, delayLevel);
    }

    /**
     * 同步延迟发送
     *
     * @param topic      主题
     * @param payload    消息体
     * @param headers    消息头
     * @param delayLevel 延迟级别,0代表不延迟，2代表延迟5秒，参考官方文档定时消息
     *                   https://github.com/apache/rocketmq/blob/master/docs/cn/features.md
     * @return void
     */
    public void sendWithDelay(String topic, Object payload, String tag, Map<String, Object> headers, int delayLevel) {
        MessageBuilder<?> builder = MessageBuilder.withPayload(payload);
        if (!headers.isEmpty()) {
            headers.forEach((k, v) -> {
                builder.setHeader(k, v);
            });
        }
        Message<?> message = builder.build();
        if (StringUtils.isNotBlank(tag))
            topic = new StringBuilder(topic).append(":").append(tag).toString();
        rocketMQTemplate.syncSend(topic, message, rocketMQTemplate.getProducer().getSendMsgTimeout(), delayLevel);
    }
}
