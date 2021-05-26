package com.ckontur.pkr.common.model;

import com.ckontur.pkr.common.exception.InvalidEnumException;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

@Getter
public enum Authority implements GrantedAuthority {
    INTERNAL,
    ADMIN,
    OPERATOR,
    CRM,
    EXAMINEE;

    @Override
    public String getAuthority() {
        return name();
    }

    public static Authority of(String value) {
        return Arrays.stream(values())
            .filter(r -> r.name().equals(value.toUpperCase()))
            .findAny()
            .orElseThrow(() -> new InvalidEnumException("Роли " + value + " не существует."));
    }

}
