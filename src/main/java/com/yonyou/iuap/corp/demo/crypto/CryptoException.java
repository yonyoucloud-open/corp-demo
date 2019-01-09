package com.yonyou.iuap.corp.demo.crypto;


/**
 * 加解密、加签验签相关的异常
 */
public class CryptoException extends RuntimeException {

    private ErrorCode errorCode;

    public CryptoException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CryptoException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(Throwable cause) {
        super(cause);
    }

    protected CryptoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        if (getErrorCode() != null) {
            return getErrorCode().getDisplayName() + ", cause: " + getCause();
        }
        return super.toString();
    }
}
