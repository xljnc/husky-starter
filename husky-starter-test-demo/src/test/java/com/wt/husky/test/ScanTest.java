package com.wt.husky.test;

import com.wt.husky.redis.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author qiyu
 * @date 2022/3/20
 */
@SpringBootTest
public class ScanTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void createData() {
        for (int i = 0; i < 10000; i++) {
            redisUtil.setStringValue("key" + i, String.valueOf(i));
        }
    }
}
