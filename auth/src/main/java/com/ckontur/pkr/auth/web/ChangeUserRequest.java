package com.ckontur.pkr.auth.web;

import com.ckontur.pkr.common.model.Authority;
import com.ckontur.pkr.common.validator.AtLeastOneNotEmpty;
import io.vavr.collection.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@AtLeastOneNotEmpty
public class ChangeUserRequest {
    private String login;
    @Length(min = 6)
    private String password;
    private Set<Authority> authorities;
}
