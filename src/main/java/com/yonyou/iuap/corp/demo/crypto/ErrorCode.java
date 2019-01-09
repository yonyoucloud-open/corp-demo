package com.yonyou.iuap.corp.demo.crypto;

public enum ErrorCode {

    SHA1_SIGN_FAILED(93003, "SHA1 签名生成失败"),
    INVALID_AES_SYMMETRIC_KEY(93004, "AES SymmetricKey 非法"),
    INVALID_SUITE_KEY(93005, "suiteKey 校验失败"),
    AES_ENCRYPT_FAILED(93006, "aes 加密失败"),
    AES_DECRYPT_FAILED(93007, "aes 解密失败"),
    INVALID_SIGNATURE(93008, "签名校验失败"),
    ENCRYPT_MESSAGE_SERIALIZE_FAILED(93009, "加密消息序列化失败"),
    ENCRYPT_MESSAGE_DESERIALIZE_FAILED(93010, "加密消息反序列化失败");

    private int code;

    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDisplayName() {
        return "Code: " + this.code + " SuiteLog: " + this.message;
    }
}
