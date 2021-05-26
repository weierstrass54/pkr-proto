package com.ckontur.pkr.exam.web;

import com.ckontur.pkr.common.validator.AtLeastOneNotEmpty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class ExamRequests {
    @Data
    public static class CreateExam {
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

        @NotNull(message = "Поле skippable не указано.")
        private boolean skippable;

        @NotNull(message = "Поле previousable не указано.")
        private boolean previousable;

        @NotEmpty(message = "Поле questionIds должно быть непустым.")
        private List<Long> questionIds;

        public Duration getDuration() {
            return Duration.ofMinutes(duration);
        }
    }

    @Data
    @AtLeastOneNotEmpty
    public static class ChangeExam {
        @Min(value = 10, message = "Длительность экзамена не может быть меньше 10 минут.")
        private Integer duration;

        @Min(value = 1, message = "Кол-во баллов за правильный ответ не может быть меньше 1.")
        private Integer pointsPerCorrect;

        @Min(value = 1, message = "Процент порога сдачи экзамена не может быть меньше 1.")
        @Max(value = 100, message = "Процент порога сдачи экзамена не может быть больше 100.")
        private Integer percentPassed;
        private Boolean skippable;
        private Boolean previousable;
        private List<Long> questionIds;

        public Optional<Duration> getDuration() {
            return Optional.ofNullable(duration).map(Duration::ofMinutes);
        }
    }
}