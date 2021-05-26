package com.ckontur.pkr.core.web;

import com.ckontur.pkr.common.validator.AtLeastOneNotEmpty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AssessmentRequests {

    @Data
    public static class CreateAssessment {
        @NotEmpty(message = "Поле name должно быть непустым.")
        private String name;

        @NotEmpty(message = "Поле address должно быть непустым.")
        private String address;

        @NotNull(message = "Поле regionId должно быть указано.")
        private Long regionId;

        @NotNull(message = "Поле placeCount должно быть непустымю")
        @Min(value = 1, message = "Значение capacity должно быть больше 0.")
        private Integer capacity;
    }

    @Data
    @AtLeastOneNotEmpty
    public static class UpdateAssessment {
        private String name;
        private String address;
        private Long regionId;
        @Min(value = 1, message = "Значение capacity должно быть больше 0.")
        private Integer capacity;
    }
}
