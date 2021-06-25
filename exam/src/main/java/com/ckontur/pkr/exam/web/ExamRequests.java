package com.ckontur.pkr.exam.web;

import com.ckontur.pkr.common.validator.AtLeastOneNotEmpty;
import io.vavr.control.Option;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;

public class ExamRequests {
    @Data
    public static class CreateExam {
        @NotNull(message = "Поле qualificationId должно быть непустым.")
        @Min(value = 1, message = "Идентификатор квалификации не может быть меньше 1.")
        private int qualificationId;

        @NotNull(message = "Поле levelId должно быть непустым.")
        @Min(value = 1, message = "Идентификатор уровня не может быть меньше 1.")
        private int levelId;

        @NotNull(message = "Поле duration должно быть непустым.")
        @Min(value = 10, message = "Длительность экзамена не может быть меньше 10 минут.")
        private int duration;

        @NotNull(message = "Поле pointsPerCorrect должно быть непустым.")
        @Min(value = 1, message = "Кол-во баллов за правильный ответ не может быть меньше 1.")
        private Integer pointsPerCorrect;

        @NotNull(message = "Поле percentPassed должно быть непустым.")
        @Min(value = 1, message = "Процент порога сдачи экзамена не может быть меньше 1.")
        @Max(value = 100, message = "Процент порога сдачи экзамена не может быть больше 100.")
        private int percentPassed;

        private Boolean skippable;
        private Boolean previousable;
        private Boolean isPublished;

        public Duration getDuration() {
            return Duration.ofMinutes(duration);
        }

        public Boolean isSkippable() {
            return Option.of(skippable).getOrElse(true);
        }
        public Boolean isPreviousable() {
            return Option.of(previousable).getOrElse(true);
        }
        public Boolean isPublished() {
            return Option.of(isPublished).getOrElse(false);
        }
    }

    @Data
    @AtLeastOneNotEmpty
    public static class ChangeExam {
        @Min(value = 1, message = "Идентификатор квалификации не может быть меньше 1.")
        private Integer qualificationId;

        @Min(value = 1, message = "Идентификатор уровня не может быть меньше 1.")
        private Integer levelId;

        @Min(value = 10, message = "Длительность экзамена не может быть меньше 10 минут.")
        private Integer duration;

        @Min(value = 1, message = "Кол-во баллов за правильный ответ не может быть меньше 1.")
        private Integer pointsPerCorrect;

        @Min(value = 1, message = "Процент порога сдачи экзамена не может быть меньше 1.")
        @Max(value = 100, message = "Процент порога сдачи экзамена не может быть больше 100.")
        private Integer percentPassed;
        private Boolean skippable;
        private Boolean previousable;
        private Boolean isPublished;

        public Option<Duration> getDuration() {
            return Option.of(duration).map(Duration::ofMinutes);
        }
    }

    @Data
    public static class ExamQuestions {
        @NotEmpty(message = "Поле questionIds должно быть непустым.")
        private List<Long> questionIds;
    }
}
