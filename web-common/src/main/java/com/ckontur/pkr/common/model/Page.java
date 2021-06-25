package com.ckontur.pkr.common.model;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Page<T> {
    @Getter
    private final Seq<T> items;
    private final int size;
    @Getter
    private final long total;

    public static <T> Page<T> of(Seq<T> items, int size, Long total) {
        return new Page<>(items, size, total);
    }

    public static <T> Page<T> empty() {
        return new Page<>(List.empty(), 0, 0);
    }

    public long getTotalPages() {
        return total % size == 0 ? total / size : (total / size) + 1;
    }
}
