package com.ckontur.pkr.exam.web;

import com.ckontur.pkr.exam.model.question.Option;
import com.ckontur.pkr.exam.model.question.Question;
import com.ckontur.pkr.exam.validator.ValidOptions;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

public class QuestionRequests {

    @Data
    @ValidOptions(typeField = "type", optionsField = "options")
    public abstract static class CreateQuestion {
        @NotEmpty(message = "Поле type должно быть непустым.")
        private Question.Type type;
        @NotEmpty(message = "Поле text должно быть непустым.")
        private String text;
        private List<CreateOption> options;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateSingleOrMultipleQuestion extends CreateQuestion {
        private List<Integer> answers;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateSequenceQuestion extends CreateQuestion {
        private List<Integer> answers;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class CreateMatchQuestion extends CreateQuestion {
        private Map<Integer, Integer> answers;
    }

    @Data
    public static class CreateOption {
        private Option.Type type;
        private String text;
    }


}
