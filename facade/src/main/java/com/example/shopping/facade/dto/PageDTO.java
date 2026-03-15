package com.example.shopping.facade.dto;

import java.util.List;

/**
 * 分页响应 DTO
 * <p>
 * 用于封装分页查询结果，避免依赖 Spring Data。
 * </p>
 *
 * @param <T> 数据类型
 */
public record PageDTO<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    /**
     * 判断是否有下一页
     */
    public boolean hasNext() {
        return !last;
    }

    /**
     * 判断是否有上一页
     */
    public boolean hasPrevious() {
        return !first;
    }
}