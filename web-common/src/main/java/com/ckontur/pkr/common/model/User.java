package com.ckontur.pkr.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vavr.collection.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Getter private Long id;
    @Getter private String login;
    private String password;
    @Getter private Set<Authority> authorities;

    public User(String login, String password, Set<Authority> authorities) {
        this(0L, login, password, authorities);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @JsonProperty("login")
    public String getUsername() {
        return login;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

}
