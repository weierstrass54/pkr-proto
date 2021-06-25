package com.ckontur.pkr.exam.model;

import com.ckontur.pkr.common.exception.InvalidEnumException;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public abstract class Question {
    private final Long id;
    private final Type type;
    private final String text;

    public abstract boolean isEmpty();

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        SINGLE(1), MULTIPLE(2), SEQUENCE(3), MATCHING(4);

        private final int value;

        public static Type of(int value) {
            return Option.ofOptional(
                Stream.of(values()).filter(t -> t.value == value).findAny()
            ).getOrElseThrow(() -> new InvalidEnumException("Типа вопроса " + value + " не существует."));
        }

        public static Type of(String value) {
            return Option.ofOptional(
                Stream.of(values()).filter(t -> t.name().toUpperCase(Locale.US).equals(value)).findAny()
            ).getOrElseThrow(() -> new InvalidEnumException("Типа вопроса " + value + " не существует."));
        }
    }
}
