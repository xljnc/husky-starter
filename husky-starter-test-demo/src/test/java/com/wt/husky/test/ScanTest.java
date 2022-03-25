package com.wt.husky.test;

import com.wt.husky.redis.util.RedisUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyu
 * @date 2022/3/20
 */
@SpringBootTest
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScanTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    @BeforeAll
    public void createData() {
        Map<String, String> people1 = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            redisUtil.setString("key" + i, String.valueOf(i), 1800L, TimeUnit.SECONDS);
            people1.put("key" + i, "value" + i);
            redisUtil.setHash("people2", "key" + i, "value" + i);
        }
        redisUtil.setObject("people1", people1);
    }

    @Test
    public void scan() {
        Set<String> keys = redisUtil.scan("key*", 1000);
        System.out.println(keys);
        System.out.println(keys.size());
    }

}
