package com.wt.husky.feign.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * description
 *
 * @author wutian2@myhexin.com
 * @date 2022/3/11
 */
@Slf4j
public class ResponseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            Response response = chain.proceed(request);
            log.info("Response protocol:{}" + response.protocol());
            return response;
        } catch (Exception e) {
            log.error("Openfeign请求异常", e);
            throw e;
        }
    }
}
