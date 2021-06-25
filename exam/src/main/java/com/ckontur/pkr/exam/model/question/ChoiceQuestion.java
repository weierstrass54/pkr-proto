package com.ckontur.pkr.exam.model.question;

import io.vavr.collection.List;
import lombok.Getter;

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
