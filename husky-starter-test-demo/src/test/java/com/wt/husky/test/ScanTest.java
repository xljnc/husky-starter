package com.wt.husky.test;

import com.wt.husky.redis.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyu
 * @date 2022/3/20
 */
@SpringBootTest
@ActiveProfiles("local")
public class ScanTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void createData() {
        for (int i = 0; i < 10000; i++) {
            redisUtil.setString("key" + i, String.valueOf(i), 1800L, TimeUnit.SECONDS);
        }
    }
}
