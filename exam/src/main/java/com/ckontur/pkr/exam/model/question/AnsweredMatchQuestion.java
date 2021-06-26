package com.ckontur.pkr.exam.model.question;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Getter;

@Getter
public class AnsweredMatchQuestion extends MatchQuestion {
    private final List<MatchAnswer> answers;

    public AnsweredMatchQuestion(Long id, Type type, String text, List<Option> options, List<MatchAnswer> answers) {
        super(id, type, text, options);
        this.answers = answers;
    }

    public Map<Long, Long> getAnswers() {
        return answers.map(a -> new Tuple2<>(a.getLeftId(), a.getRightId())).collect(HashMap.collector());
    }
}
