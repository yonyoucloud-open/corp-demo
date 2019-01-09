package com.yonyou.iuap.corp.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponse {

    /**
     * 获取的访问令牌 access_token
     */
    @JsonProperty(value = "access_token")
    private String accessToken;

    /**
     * 访问令牌的过期时间，单位秒
     */
    private long expire;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
