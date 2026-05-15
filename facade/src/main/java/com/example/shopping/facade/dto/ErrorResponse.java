package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "错误响应")
public record ErrorResponse(
    @Schema(description = "错误发生时间") String timestamp,
    @Schema(description = "HTTP 状态码", example = "400") int status,
    @Schema(description = "错误类别", example = "业务错误") String error,
    @Schema(description = "错误详情", example = "用户不存在") String message,
    @Schema(description = "错误码", example = "BUSINESS_ERROR") String code,
    @Schema(description = "详细错误信息（可选）") Object details
) {}
