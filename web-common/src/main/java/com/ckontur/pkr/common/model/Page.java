package com.ckontur.pkr.common.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Page<T> {
    @Getter
    private final Collection<T> items;
    private final int size;
    @Getter
    private final long total;

    public static <T> Page<T> of(Collection<T> items, int size, Long total) {
        return new Page<>(items, size, total);
    }

    public static <T> Page<T> empty() {
        return new Page<>(Collections.emptyList(), 0, 0);
    }

    public long getTotalPages() {
        return total % size == 0 ? total / size : (total / size) + 1;
    }
}
