package com.retrip.trip.infra.adapter.util;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public abstract class PaginationUtils {

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
