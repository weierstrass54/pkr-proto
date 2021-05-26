package com.ckontur.pkr.record.web;

import com.ckontur.pkr.common.model.Authority;
import com.ckontur.pkr.common.validator.AtLeastOneNotEmpty;
import com.ckontur.pkr.common.validator.Login;
import com.ckontur.pkr.common.validator.Password;
import com.ckontur.pkr.common.validator.Phone;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserRequests {
    @Data
    public static class CreateUser {
        @Login
        private String login;
        @Password
        private String password;
        @NotNull
        private Authority authority;

        @NotEmpty(message = "Поле firstName должно быть непустым.")
        private String firstName;

        @NotEmpty(message = "Поле middleName должно быть непустым.")
        private String middleName;

        @NotEmpty(message = "Поле lastName должно быть непустым.")
        private String lastName;

        @NotEmpty(message = "Поле phone должно быть непустым.")
        private String phone;

        @Email
        @NotEmpty(message = "Поле email должно быть непустым.")
        private String email;
        private String employer;
        private String appointment;
    }

    @Data
    @AtLeastOneNotEmpty
    public static class ChangeUser {
        @Login(nullable = true) private String login;
        @Password(nullable = true) private String password;
        private Authority authority;
        private String firstName;
        private String middleName;
        private String lastName;
        @Phone
        private String phone;
        @Email private String email;
        private String employer;
        private String appointment;
    }
}
