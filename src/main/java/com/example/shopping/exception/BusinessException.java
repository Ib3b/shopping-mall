package com.example.shopping.exception;

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
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }
}