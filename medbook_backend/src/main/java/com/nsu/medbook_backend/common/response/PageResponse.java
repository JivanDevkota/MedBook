package com.nsu.medbook_backend.common.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Stable pagination envelope decoupled from Spring Data's Page serialization.
 */
public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int size,
        int number,
        boolean first,
        boolean last,
        boolean empty
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}
