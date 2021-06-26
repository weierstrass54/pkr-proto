package com.ckontur.pkr.exam.model.question;

import io.vavr.collection.List;
import lombok.Getter;

@Getter
public class AnsweredChoiceQuestion extends ChoiceQuestion {
    private final List<ChoiceAnswer> answers;

    public AnsweredChoiceQuestion(Long id, Type type, String text, List<Option> options, List<ChoiceAnswer> answers) {
        super(id ,type, text, options);
        this.answers = answers;
    }

    public List<Long> getAnswers() {
        return answers.map(ChoiceAnswer::getId);
    }
}
