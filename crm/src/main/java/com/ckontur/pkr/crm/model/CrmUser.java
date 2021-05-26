package com.ckontur.pkr.crm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrmUser {
    private Long id;
    private String login;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;
}
