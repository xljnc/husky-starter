package com.wt.husky.test;

import cn.hutool.core.date.DateUtil;
import com.wt.husky.redis.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyu
 * @date 2022/3/20
 */
@SpringBootTest
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

//    @Test
//    @BeforeAll
//    public void createData() {
//        Map<String, String> people1 = new HashMap<>();
//        for (int i = 0; i < 10000; i++) {
//            redisUtil.setString("key" + i, String.valueOf(i), 1800L, TimeUnit.SECONDS);
//            people1.put("key" + i, "value" + i);
//            redisUtil.setHash("people2", "key" + i, "value" + i);
//        }
//        redisUtil.setObject("people1", people1);
//    }

    @Test
    public void testGetSet() {
        redisUtil.setString("sk", "sv");
        System.out.println(redisUtil.getString("sk"));
    }

    @Test
    public void scan() {
        for (int i = 0; i < 10000; i++) {
            redisUtil.setString("key" + i, String.valueOf(i), 1800L, TimeUnit.SECONDS);
        }
        Set<String> keys = redisUtil.scan("key*", 1000);
        System.out.println(keys);
        System.out.println(keys.size());
    }

    @Test
    public void testGetMultiHashValues() {
        for (int i = 0; i < 10000; i++) {
            redisUtil.setHash("people2", "key" + i, "value" + i);
        }
        List<Object> hashKeys = Arrays.asList("key1", "key2", "key3");
        List<Object> values = redisUtil.getMultiHashValues("people2", hashKeys);
        System.out.println(hashKeys);
        System.out.println(values);
    }

    @Test
    public void testScanHash() {
        for (int i = 0; i < 10000; i++) {
            redisUtil.setHash("people2", "key" + i, "value" + i);
        }
        Set<Map.Entry<Object, Object>> entries = redisUtil.scanHash("people2", "key*", 1000);
        System.out.println(entries.size());
        System.out.println(entries);
    }

    @Test
    public void testHyperLogLog() {
        for (int i = 0; i < 10000; i++) {
            redisUtil.addHyperLogLogValue("pfKey1", "key" + i);
            redisUtil.addHyperLogLogValue("pfKey2", "key" + i);
        }
        redisUtil.mergeHyperLogLog("pfKey3", "pfKey1", "pfKey2");
        System.out.println(redisUtil.sizeOfHyperLogLog("pfKey1"));
        System.out.println(redisUtil.sizeOfHyperLogLog("pfKey2"));
        System.out.println(redisUtil.sizeOfHyperLogLog("pfKey3"));
    }

    @Test
    public void testBitSign() {
        String userId = "sign::1";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        Calendar beginOfMonth = DateUtil.beginOfMonth(Calendar.getInstance());
        int dayOffset = DateUtil.thisDayOfMonth();
        String month = DateUtil.format(DateUtil.date(), formatter);
        String hashKeyMonth = "1::" + month;
        Random rad = new Random();
        for (int offset = 0; offset < dayOffset; offset++) {
            redisUtil.setBit(hashKeyMonth, offset, rad.nextBoolean());
        }
        //判断当天是否打卡
        boolean signedDay = redisUtil.getBit(hashKeyMonth, dayOffset - 1);
        System.out.println(signedDay);
        boolean signedMonth = true;
        for (int offset = 0; offset < dayOffset; offset++) {
            signedMonth = redisUtil.getBit(hashKeyMonth, offset);
            if (!signedMonth)
                break;
        }
        System.out.println(signedMonth);
        signedMonth = redisUtil.bitCount(hashKeyMonth, 0, dayOffset - 1).equals(Long.valueOf(dayOffset));
        System.out.println(signedMonth);
        redisUtil.setHash(userId, month, redisUtil.getString(hashKeyMonth));

    }

    @Test
    public void testGeo() {
        Map<String, double[]> members = new HashMap<>();
        members.put("alibaba", new double[]{120.034568, 30.285501});
        members.put("zheda", new double[]{120.08764, 30.308996});
        redisUtil.batchAddGeo("company", members);
        List<Double[]> positions = redisUtil.geoPosition("company", "alibaba", "zheda");
        positions.forEach(x -> System.out.println(x[0] + "," + x[1]));
        List<String> hashes = redisUtil.geoHash("company", "alibaba", "zheda");
        hashes.forEach(x -> System.out.println(x));
        Double distance = redisUtil.geoDistance("company", "alibaba", "zheda", RedisUtil.MetricUnit.KILOMETERS);
        System.out.println("distance:" + distance);

    }

    @Test
    public void testSet() {
        redisUtil.addToSet("test-set", "a", "b");
        Set<String> set = redisUtil.scanSet("test-set", null, 10);
        System.out.println(set);
        redisUtil.removeFromSet("test-set", "a", "b");
        set = redisUtil.scanSet("test-set", null, 10);
        System.out.println(set);
        redisUtil.deleteKey("test-set");
        redisUtil.removeFromSet("test-set", "a", "b");

    }
}
