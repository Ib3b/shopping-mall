package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "分页响应")
public record PageDTO<T>(
    @Schema(description = "数据列表") List<T> content,
    @Schema(description = "当前页码（从0开始）", example = "0") int pageNumber,
    @Schema(description = "每页大小", example = "10") int pageSize,
    @Schema(description = "总记录数", example = "100") long totalElements,
    @Schema(description = "总页数", example = "10") int totalPages,
    @Schema(description = "是否第一页", example = "true") boolean first,
    @Schema(description = "是否最后一页", example = "false") boolean last
) {
    public boolean hasNext() { return !last; }
    public boolean hasPrevious() { return !first; }
}
