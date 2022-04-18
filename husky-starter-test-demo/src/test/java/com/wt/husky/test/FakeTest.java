package com.wt.husky.test;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * description
 *
 * @author wutian2@myhexin.com
 * @date 2022/4/18
 */
@SpringBootTest
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FakeTest {
}
