package com.wt.husky.feign.client;

import com.wt.husky.feign.config.FeignHttpConfig;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

import java.net.URI;

/**
 * @author 一贫
 * @date 2021/8/18
 */
@FeignClient(name = "base", configuration = FeignHttpConfig.class)
public interface FeignBaseClient {

    @RequestLine("POST")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    String post(URI uri);

    @RequestLine("GET")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    String get(URI uri);

}
