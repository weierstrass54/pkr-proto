package com.ckontur.pkr.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Qualification {
    private final Long id;
    private final String name;

    public Qualification(String name) {
        this(0L, name);
    }
}
