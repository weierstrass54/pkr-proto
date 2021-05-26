package com.ckontur.pkr.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Assessment {
    private final Long id;
    private final String name;
    private final String region;
    private final String address;
    private final Integer capacity;
}
