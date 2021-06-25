package com.ckontur.pkr.exam.model.question;

import io.vavr.collection.List;
import lombok.Getter;

@Getter
public class AnsweredChoiceQuestion extends ChoiceQuestion {
    private final List<Long> answers;

    public AnsweredChoiceQuestion(Long id, Type type, String text, List<Option> options, List<Long> answers) {
        super(id ,type, text, options);
        this.answers = answers;
    }
}
