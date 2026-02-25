package com.shinhan.spp.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PageResponse<T> {
    private T items;
    private Integer page;
    private Integer pageSize;
    private Integer totalCount;

    public static <T> PageResponse<T> response(final T item, final int page, final int pageSize, final Integer totalCount) {
        return PageResponse.<T>builder()
                .items(item)
                .page(page)
                .pageSize(pageSize)
                .totalCount(totalCount == null ? 0 : totalCount)
                .build();
    }
}
