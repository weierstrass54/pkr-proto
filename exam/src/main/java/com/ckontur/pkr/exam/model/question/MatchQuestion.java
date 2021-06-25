package com.ckontur.pkr.exam.model.question;

import io.vavr.collection.List;
import lombok.Getter;

@Getter
public class MatchQuestion extends Question {
    private final List<Option> leftOptions;
    private final List<Option> rightOptions;

    public MatchQuestion(Long id, Type type, String text, List<Option> options) {
        super(id, type, text);
        this.leftOptions = options.filter(o -> o.getType() == Option.Type.LEFT);
        this.rightOptions = options.filter(o -> o.getType() == Option.Type.RIGHT);
    }

    @Override
    public boolean isEmpty() {
        return leftOptions.isEmpty() && rightOptions.isEmpty();
    }
}
