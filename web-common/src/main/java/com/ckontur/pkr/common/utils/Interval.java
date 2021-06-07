package com.ckontur.pkr.common.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Interval<T extends Comparable<? super T>> {
    private final T start;
    private final T finish;
    private final boolean includeStart;
    private final boolean includeFinish;

    public static <T extends Comparable<T>> Interval<T> ofOpened(T start, T finish) {
        return of(start, finish, false, false);
    }

    public static <T extends Comparable<T>> Interval<T> ofClosed(T start, T finish) {
        return of(start, finish, true, true);
    }

    public static <T extends Comparable<? super T>> Interval<T> of(T start, T finish, boolean includeStart, boolean includeFinish) {
        if (start == null || finish == null) {
            throw new IllegalArgumentException("Начало и конец интервала не могут быть null.");
        }
        if (start.equals(finish)) {
            throw new IllegalArgumentException("Начало и конец интервала не должны совпадать.");
        }
        if (start.compareTo(finish) > 0) {
            throw new IllegalArgumentException("Конец интервала должен быть больше, чем начало.");
        }
        return new Interval<>(start, finish, includeStart, includeFinish);
    }

    public boolean intersects(Interval<T> interval) {
        return (includeStart ? interval.finish.compareTo(start) >= 0 : interval.finish.compareTo(start) > 0) &&
            (includeFinish ? interval.start.compareTo(finish) >= 0 : interval.start.compareTo(finish) > 0);
    }

    public boolean contains(Interval<T> interval) {
        return (includeStart ? interval.start.compareTo(start) >= 0 : interval.start.compareTo(start) > 0) &&
            (includeFinish ? interval.finish.compareTo(finish) >= 0 : interval.finish.compareTo(finish) > 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interval<?> interval = (Interval<?>) o;
        return includeStart == interval.includeStart && includeFinish == interval.includeFinish &&
            Objects.equals(start, interval.start) && Objects.equals(finish, interval.finish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, finish, includeStart, includeFinish);
    }
}
