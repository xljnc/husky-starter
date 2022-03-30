package com.wt.husky.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
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
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * @param key
     * @return boolean 删除key
     * @description
     */
    public boolean deleteKey(String key) {
        return stringRedisTemplate.delete(key);
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
        Set<String> keys = stringRedisTemplate.keys(prefix + "*");
        return stringRedisTemplate.delete(keys);
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
        Set<String> keys = stringRedisTemplate.keys("*" + suffix);
        return stringRedisTemplate.delete(keys);
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
     * 设置String类型的Value
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     * @return void
     */
    public void setString(String key, String value, Long expireTime, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, expireTime, timeUnit);
    }

    /**
     * 获取过期时间
     *
     * @param key
     * @param timeUnit
     * @return java.lang.Long
     */
    public Long getExpireTime(String key, TimeUnit timeUnit) {
        return stringRedisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 设置过期时间
     *
     * @param key        key
     * @param expireTime 过期时间
     * @param timeUnit   时间格式
     * @return java.lang.Boolean 是否成功
     */
    public Boolean setExpireTime(String key, Long expireTime, TimeUnit timeUnit) {
        return stringRedisTemplate.expire(key, expireTime, timeUnit);
    }

    /**
     * 存储可序列化对象
     *
     * @param key
     * @param value
     * @param expireTime
     * @param timeUnit
     */
    public void setObject(String key, Object value, Long expireTime, TimeUnit timeUnit) {
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
     * 获取对象
     **/
    public Object getObject(String key) {
        return jacksonRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获取指定类型对象
     **/
    public <T> T getGenericObject(String key) {
        return (T) jacksonRedisTemplate.opsForValue().get(key);
    }

    /**
     * 扫描key
     *
     * @param pattern 扫描模式
     * @param count   每次扫描的数量
     * @return java.util.Set<java.lang.String>
     */
    public Set<String> scan(String pattern, long count) {
        Set<String> keys = new HashSet<>();
        Cursor<byte[]> cursor = null;
        try {
            cursor = stringRedisTemplate.executeWithStickyConnection(connection ->
                    connection.scan(ScanOptions.scanOptions().match(pattern).count(count).build())
            );
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return keys;
    }

    /**
     * 存储Hash
     *
     * @param key     key
     * @param hashKey hash Key
     * @param value   hash value
     */
    public void setHash(String key, String hashKey, Object value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取hash key存储值
     *
     * @param key     key
     * @param hashKey hash Key
     */
    public <T> T getHash(String key, String hashKey) {
        return (T) stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * @param key     key
     * @param hashKey hash键
     * @param value   hash值
     * @return java.lang.Boolean 是否成功
     */
    public Boolean setHashValueIfAbsent(String key, String hashKey, Object value) {
        return stringRedisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 是否存在 hash key
     *
     * @param key     key
     * @param hashKey hash键
     * @return java.lang.Boolean 是否存在key
     */
    public Boolean containsHashKey(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 批量获取 hash key
     *
     * @param key      key
     * @param hashKeys hash键集合
     * @return
     */
    public List<Object> getMultiHashValues(String key, Collection<Object> hashKeys) {
        return stringRedisTemplate.opsForHash().multiGet(key, hashKeys);
    }

    /**
     * 扫描key
     *
     * @param key     key
     * @param pattern 扫描模式
     * @param count   每次扫描的数量
     * @return hash中的键值对
     */
    public Set<Map.Entry<Object, Object>> scanHash(String key, String pattern, long count) {
        Cursor<Map.Entry<Object, Object>> cursor = null;
        Set<Map.Entry<Object, Object>> entries = new HashSet<>();
        try {
            cursor = stringRedisTemplate.opsForHash().scan(key,
                    ScanOptions.scanOptions().match(pattern).count(count).build());
            while (cursor.hasNext()) {
                entries.add(cursor.next());
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return entries;
    }

    /**
     * 判断Set是否包含指定对象
     *
     * @param key       key
     * @param candidate 对象
     * @return java.lang.Boolean
     */
    public Boolean isSetMember(String key, String candidate) {
        return stringRedisTemplate.opsForSet().isMember(key, candidate);
    }

    /**
     * 扫描key
     *
     * @param key     key
     * @param pattern 扫描模式
     * @param count   每次扫描的数量
     * @return set中的值
     */
    public Set<String> scanSet(String key, String pattern, long count) {
        Cursor<String> cursor = null;
        Set<String> values = new HashSet<>();
        try {
            cursor = stringRedisTemplate.opsForSet().scan(key,
                    ScanOptions.scanOptions().match(pattern).count(count).build());
            while (cursor.hasNext()) {
                values.add(cursor.next());
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return values;
    }

    /**
     * value加入到zset
     *
     * @param key   key
     * @param value value
     * @param score 分数
     * @return java.lang.Boolean 是否添加成功
     */
    public Boolean addZSetValue(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * value加入到HyperLogLog
     *
     * @param key    key
     * @param values values
     * @return java.lang.Long 添加成功的个数
     */
    public Long addHyperLogLogValue(String key, String... values) {
        return stringRedisTemplate.opsForHyperLogLog().add(key, values);
    }

    /**
     * HyperLogLog基数
     * 如果是多个HyperLogLog，则返回基数估值之和
     *
     * @param keys    keys
     * @return java.lang.Long 基数估值之和
     */
    public Long hyperLogLogValueSize(String... keys) {
        return stringRedisTemplate.opsForHyperLogLog().size(keys);
    }
}
