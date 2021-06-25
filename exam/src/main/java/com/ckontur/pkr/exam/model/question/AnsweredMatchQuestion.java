package com.ckontur.pkr.exam.model.question;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Getter;

@Getter
public class AnsweredMatchQuestion extends MatchQuestion {
    private final Map<Long, Long> answers;

    public AnsweredMatchQuestion(Long id, Type type, String text, List<Option> options, Map<Long, Long> answers) {
        super(id, type, text, options);
        this.answers = answers;
    }
}
