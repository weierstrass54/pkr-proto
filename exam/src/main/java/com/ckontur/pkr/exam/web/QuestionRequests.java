package com.ckontur.pkr.exam.web;

import com.ckontur.pkr.exam.model.Option;
import com.ckontur.pkr.exam.model.Question;
import com.ckontur.pkr.exam.validator.ValidOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

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
