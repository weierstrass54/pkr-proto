package com.ckontur.pkr.exam.web;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateOrUpdateByNameRequest {
    @NotEmpty(message = "Поле name должно быть непустым.")
    private String name;
}
