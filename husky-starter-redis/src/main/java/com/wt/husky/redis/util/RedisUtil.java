package com.wt.husky.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Qualifier("jacksonRedisTemplate")
    private RedisTemplate<String, Object> jacksonRedisTemplate;

    @Autowired
    @Qualifier("stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param key
     * @return boolean 如果key存在返回true，否则返回false
     * @description 检查Key是否存在
     */
    public boolean existsKey(String key) {
        return jacksonRedisTemplate.hasKey(key);
    }

    /**
     * @param key
     * @return boolean 删除key
     * @description
     */
    public boolean deleteKey(String key) {
        return jacksonRedisTemplate.delete(key);
    }

    /**
     * 通过前缀模糊匹配删除Key
     * 注意：这是个危险操作，因为keys命令的复杂度是O(n),执行速度非常慢
     * Redis的单线程模型决定了在执行完keys命令前是阻塞的
     * key数量大的情况下非常危险， 酌情使用
     *
     * @deprecated As of JDK version 8
     **/
    @Deprecated(since = "jdk8")
    public Long deleteByPrefix(String prefix) {
        Set<String> keys = jacksonRedisTemplate.keys(prefix + "*");
        return jacksonRedisTemplate.delete(keys);
    }

    /**
     * 通过后缀模糊匹配删除Key
     * 注意：这是个危险操作，因为keys命令的复杂度是O(n),执行速度非常慢
     * Redis的单线程模型决定了在执行完keys命令前是阻塞的
     * key数量大的情况下非常危险， 酌情使用
     *
     * @deprecated As of JDK version 8
     **/
    @Deprecated(since = "jdk8")
    public Long deleteBySuffix(String suffix) {
        Set<String> keys = jacksonRedisTemplate.keys("*" + suffix);
        return jacksonRedisTemplate.delete(keys);
    }


    /**
     * 获取String类型的Value
     **/
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * @param key
     * @param value
     * @return void
     * @description 设置String类型的Value
     */
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return void
     * @description 设置String类型的Value
     */
    public void setString(String key, String value, Long expireTime, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    /**
     * @param key
     * @param timeUnit
     * @return java.lang.Long
     * @description 获取key的过期时间
     */
    public Long getExpireTime(String key, TimeUnit timeUnit) {
        return jacksonRedisTemplate.getExpire(key, timeUnit);
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
        jacksonRedisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    /**
     * 存储可序列化对象
     *
     * @param key
     * @param value
     */
    public void setObject(String key, Object value) {
        jacksonRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取java对象
     **/
    public Object getObject(String key) {
        return jacksonRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取java对象
     **/
    public <T> T getSpecifiedObject(String key) {
        return (T) jacksonRedisTemplate.opsForValue().get(key);
    }

}
