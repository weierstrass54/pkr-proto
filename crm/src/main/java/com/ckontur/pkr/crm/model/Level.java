package com.ckontur.pkr.crm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Level {
    private final Long id;
    private final String name;

    public Level(String name) {
        this(0L, name);
    }
}
