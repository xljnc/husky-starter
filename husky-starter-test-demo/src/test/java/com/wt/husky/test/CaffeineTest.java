package com.wt.husky.test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

/**
 * description
 *
 * @author wutian2@myhexin.com
 * @date 2022/4/6
 */
@SpringBootTest
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CaffeineTest {

    @Test
    public void testInit() {
        Cache<String, String> cache = Caffeine.newBuilder().maximumSize(100).expireAfterWrite(100L, TimeUnit.SECONDS).build();
        cache.put("book", "caffeine");
        System.out.println(cache.getIfPresent("book"));
    }
}
