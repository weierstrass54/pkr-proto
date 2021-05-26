package com.ckontur.pkr.crm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Region {
    private final Long id;
    private final String name;

    public Region(String name) {
        this(0L, name);
    }
}
