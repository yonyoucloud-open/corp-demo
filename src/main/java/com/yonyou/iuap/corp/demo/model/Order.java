package com.yonyou.iuap.corp.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

    /**
     * 购买用户的友户通 userId
     */
    private String userId;

    /**
     * 云市场的订单 id，唯一标识了该订单
     */
    private String orderId;

    /**
     * 订单更新的 ts
     */
    private Long ts;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 开通用户的 email
     */
    private String email;

    /**
     * 套件过期日期 unix 时间戳，当前有效时间 + lease * 1 month
     */
    private Long expiredOn;

    /**
     * 套件订单的租期，单位为月
     */
    private Integer lease;

    /**
     * 开通用户的手机号
     */
    private String mobile;

    /**
     * 是否新购，即是首次购买该产品还是续期
     */
    private Boolean newBuy;

    /**
     * 订单的 sku id，由云市场生成，在云市场展示给 isv，需给用户
     */
    private String orderSkuId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 订单来源，目前购买的都来自 "diwork", 测试的来自 "open-test"
     */
    private String resCode;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getExpiredOn() {
        return expiredOn;
    }

    public void setExpiredOn(Long expiredOn) {
        this.expiredOn = expiredOn;
    }

    public Integer getLease() {
        return lease;
    }

    public void setLease(Integer lease) {
        this.lease = lease;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getNewBuy() {
        return newBuy;
    }

    public void setNewBuy(Boolean newBuy) {
        this.newBuy = newBuy;
    }

    public String getOrderSkuId() {
        return orderSkuId;
    }

    public void setOrderSkuId(String orderSkuId) {
        this.orderSkuId = orderSkuId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    @Override
    public String toString() {
        return "Order{" +
                "userId='" + userId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", ts=" + ts +
                ", skuName='" + skuName + '\'' +
                ", email='" + email + '\'' +
                ", expiredOn=" + expiredOn +
                ", lease=" + lease +
                ", mobile='" + mobile + '\'' +
                ", newBuy=" + newBuy +
                ", orderSkuId='" + orderSkuId + '\'' +
                ", productName='" + productName + '\'' +
                ", resCode='" + resCode + '\'' +
                '}';
    }
}
