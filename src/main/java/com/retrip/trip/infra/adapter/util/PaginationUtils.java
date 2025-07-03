package com.retrip.trip.infra.adapter.util;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public abstract class PaginationUtils {
    public static Pageable createPageRequest(Pageable pageable, String order, String sort) {
        return PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.fromString(sort), order)
        );
    }

    /**
     * 무한스크롤 위해 hasNext 체크 및 제거하는 util
     */
    public static <T> PageImpl<T> checkEndPage(Pageable pageable, List<T> results) {
        if (results.size() > pageable.getPageSize()) {
            results.remove(pageable.getPageSize());
        }
        return new PageImpl<>(results, pageable, results.size());
    }
}
