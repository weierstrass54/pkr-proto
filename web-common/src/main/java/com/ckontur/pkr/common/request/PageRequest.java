package com.ckontur.pkr.crm.model;

import com.ckontur.pkr.common.exception.InvalidEnumException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PageRequest {
    private final int page;
    private final int size;
    private final Direction direction;

    public static PageRequest of(Integer page, Integer size, Direction direction) {
        return new PageRequest(
            Optional.ofNullable(page).filter(p -> p > 0).orElse(1),
            Optional.ofNullable(size).filter(s -> s > 0).orElse(50),
            Optional.ofNullable(direction).orElse(Direction.ASC)
        );
    }

    public int getOffset() {
        return (page - 1) * size;
    }

    public enum Direction {
        ASC, DESC;

        public static Direction of(String value) {
            return Stream.of(values()).filter(
                d -> d.name().equals(value.toUpperCase(Locale.US))
            ).findAny()
            .orElseThrow(() -> new InvalidEnumException("Сортировка допускает только 'ACS' и 'DESC' значения"));
        }
    }
}
