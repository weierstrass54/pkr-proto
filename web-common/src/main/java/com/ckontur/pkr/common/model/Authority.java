package com.ckontur.pkr.common.model;

import com.ckontur.pkr.common.exception.InvalidEnumException;
import io.vavr.collection.Stream;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

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
        return Stream.of(values())
            .filter(r -> r.name().equals(value.toUpperCase()))
            .getOrElseThrow(() -> new InvalidEnumException("Роли " + value + " не существует."));
    }

}
