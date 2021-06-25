package com.wt.husky.feign.response;

import feign.FeignException;
import feign.Response;
import feign.form.util.CharsetUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author 一贫
 * @date 2021/1/7
 */
@Data
public class FeignResponse implements Serializable {
    private static final long serialVersionUID = 8869176546011426676L;

    private int status;
    private String reason;
    private Map<String, Collection<String>> headers;
    private String body;
    private String request;


    public FeignResponse(FeignException e) {
        this.status = e.status();
        this.reason = e.getMessage();
        this.body = new String(e.request().body(), CharsetUtil.UTF_8);
        this.request = e.request().toString();
    }

    public FeignResponse(Response response) {
        this.status = response.status();
        this.reason = response.reason();
        this.headers = response.headers();
        this.body = response.body().toString();
        this.request = response.request().toString();
    }

}
