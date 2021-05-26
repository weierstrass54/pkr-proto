package com.ckontur.pkr.exam.model;

import lombok.Getter;

import java.util.List;

@Getter
public class ChoiceQuestion extends Question {
    private final List<Option> options;

    public ChoiceQuestion(Long id, Type type, String text, List<Option> options) {
        super(id, type, text);
        this.options = options;
    }

    @Override
    public boolean isEmpty() {
        return options.isEmpty();
    }
}
