package com.yonyou.iuap.corp.demo.model;

import java.time.Instant;

/**
 * 记录套件租户的授权信息
 */
public class SuiteAuth {

    /**
     * 授权的套件
     */
    private String suiteKey;

    /**
     * 授权的租户 id
     */
    private String tenantId;

    /**
     * 该租户对应的访问令牌
     */
    private String accessToken;

    /**
     * 访问令牌过期时间点
     */
    private Instant expireAt;

    public void updateAccessToken(AccessTokenResponse response) {
        this.accessToken = response.getAccessToken();
        this.expireAt = Instant.now().plusSeconds(response.getExpire());
    }

    public String getSuiteKey() {
        return suiteKey;
    }

    public void setSuiteKey(String suiteKey) {
        this.suiteKey = suiteKey;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
    }
}
