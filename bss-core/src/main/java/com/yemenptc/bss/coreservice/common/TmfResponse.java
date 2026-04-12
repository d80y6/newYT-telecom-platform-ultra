package com.yemenptc.bss.coreservice.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TmfResponse<T> {
    
    private List<T> data;
    private PageInfo pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int size;
        private int totalPages;
        private long totalElements;
        private int number;
    }
    
    public static <T> TmfResponse<T> of(List<T> data) {
        return TmfResponse.<T>builder()
            .data(data)
            .build();
    }
    
    public static <T> TmfResponse<T> ofPage(Page<T> page) {
        return TmfResponse.<T>builder()
            .data(page.getContent())
            .pagination(PageInfo.builder()
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .number(page.getNumber())
                .build())
            .build();
    }
}
