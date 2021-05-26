package com.ckontur.pkr.record.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {
    private Long id;
    private String name;
    private String region;
    private String address;
    private Integer capacity;
}
