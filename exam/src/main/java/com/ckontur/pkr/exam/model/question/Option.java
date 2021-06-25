package com.ckontur.pkr.exam.model.question;

import com.ckontur.pkr.common.exception.InvalidEnumException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class Option {
    private final Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final Type type;
    private final String text;

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        ANY(0), LIST(1), LEFT(2), RIGHT(3);

        private final int value;

        public static Type of(int value) {
            return io.vavr.control.Option.ofOptional(
                Stream.of(values()).filter(t -> t.value == value).findAny()
            ).getOrElseThrow(() -> new InvalidEnumException("Типа ответа " + value + " не существует."));
        }
    }
}
