package com.ckontur.pkr.auth.web;

import com.ckontur.pkr.common.model.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    @NotEmpty(message = "Поле login должно быть непустым.")
    private String login;

    @NotEmpty(message = "Поле password должно быть непустым.")
    @Length(min=6, message = "Поле password должно иметь длину не менее 6 символов.")
    private String password;

    @NotEmpty(message = "Поле roles должно быть непустым.")
    private Set<Authority> authorities;
}
