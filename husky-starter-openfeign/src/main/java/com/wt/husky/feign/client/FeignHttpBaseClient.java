package com.wt.husky.feign.client;

import com.wt.husky.feign.config.FeignHttpConfig;
import com.wt.husky.feign.fallback.FeignHttpFallback;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

import java.net.URI;

/**
 * @author 一贫
 * @date 2021/8/18
 */
@FeignClient(name = "http-base", configuration = FeignHttpConfig.class, fallback = FeignHttpFallback.class)
public interface FeignHttpBaseClient {

    @RequestLine("POST")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    String post(URI uri);

    @RequestLine("GET")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    String get(URI uri);

}
