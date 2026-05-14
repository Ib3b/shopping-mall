package com.example.shopping.common.exception;

/**
 * 业务异常类
 * <p>
 * 用于表示业务逻辑中的可预期异常，如用户不存在、商品库存不足等。
 * 包含错误码和错误信息。
 * </p>
 */
public class BusinessException extends RuntimeException {

    private final String code;

    /**
     * 创建业务异常（使用默认错误码）
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
    }

    /**
     * 创建业务异常（使用自定义错误码）
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 创建业务异常（带原始 cause）
     *
     * @param message 错误信息
     * @param cause   原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = "BUSINESS_ERROR";
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }
}