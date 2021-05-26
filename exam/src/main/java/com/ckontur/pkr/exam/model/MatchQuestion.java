package com.ckontur.pkr.exam.model;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MatchQuestion extends Question {
    private final List<Option> leftOptions;
    private final List<Option> rightOptions;

    public MatchQuestion(Long id, Type type, String text, List<Option> options) {
        super(id, type, text);
        this.leftOptions = options.stream().filter(o -> o.getType() == Option.Type.LEFT).collect(Collectors.toList());
        this.rightOptions = options.stream().filter(o -> o.getType() == Option.Type.RIGHT).collect(Collectors.toList());
    }

    @Override
    public boolean isEmpty() {
        return leftOptions.isEmpty() && rightOptions.isEmpty();
    }
}
