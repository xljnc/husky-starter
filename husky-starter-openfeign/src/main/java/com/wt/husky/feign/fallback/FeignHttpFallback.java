package com.wt.husky.feign.fallback;

import com.wt.husky.feign.client.FeignHttpBaseClient;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * @author qiyu
 * @date 2022/3/13
 */
@Slf4j
public class FeignHttpFallback implements FeignHttpBaseClient {

    @Override
    public String post(URI uri) {
        log.error("Post请求异常，触发熔断，请求地址:{}");
        return "failed";
    }

    @Override
    public String get(URI uri) {
        log.error("Get请求异常，触发熔断，请求地址:{}");
        return "failed";
    }
}
