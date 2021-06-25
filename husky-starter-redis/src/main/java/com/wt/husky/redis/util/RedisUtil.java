package com.wt.husky.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 *
 * @author 一贫
 * @date 2020/12/29
 */
@Component
@ConditionalOnMissingBean(RedisUtil.class)
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private ListOperations<String, Object> listOperations;

    /**
     * @param key
     * @return boolean 如果key存在返回true，否则返回false
     * @description 检查Key是否存在
     */
    public boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * @param key
     * @return boolean 删除key
     * @description
     */
    public boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 通过前缀模糊匹配删除Key
     **/
    public Long deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        return redisTemplate.delete(keys);
    }

    /**
     * 通过后缀模糊匹配删除Key
     **/
    public Long deleteBySuffix(String suffix) {
        Set<String> keys = redisTemplate.keys("*" + suffix);
        return redisTemplate.delete(keys);
    }


    /**
     * 获取String类型的Value
     **/
    public String getStringValue(String key) {
        return (String) valueOperations.get(key);
    }

    /**
     * @param key
     * @param value
     * @return void
     * @description 设置String类型的Value
     */
    public void setStringValue(String key, String value) {
        valueOperations.set(key, value);
    }

    /**
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return void
     * @description 设置String类型的Value
     */
    public void setStringValue(String key, String value, Long expireTime, TimeUnit timeUnit) {
        valueOperations.set(key, value, expireTime, timeUnit);
    }

    /**
     * @param key
     * @return java.lang.Long
     * @description 获取key的过期时间，单位秒
     */
    public Long getExpireTime(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 存储可序列化对象
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     */
    public void setObjectValue(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        valueOperations.set(key, value, expireTime, timeUnit);
    }

    /**
     * 存储可序列化对象
     *
     * @param key
     * @param value
     */
    public void setObjectValue(String key, Object value) {
        valueOperations.set(key, value);
    }

    /**
     * 获取java对象
     **/
    public Object getObjectValue(String key) {
        return valueOperations.get(key);
    }

}
