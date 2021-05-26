package com.ckontur.pkr.record.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordUser {
    private Long id;
    private String login;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;
    private String employer;
    private String appointment;
}
