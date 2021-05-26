package com.ckontur.pkr.auth.service;

import com.ckontur.pkr.auth.repository.UserRepository;
import com.ckontur.pkr.auth.web.ChangeUserRequest;
import com.ckontur.pkr.auth.web.CreateUserRequest;
import com.ckontur.pkr.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getById(Long id) {
        return userRepository.getById(id);
    }

    public Optional<User> create(CreateUserRequest user) {
        String password = passwordEncoder.encode(user.getPassword());
        return userRepository.create(user.getLogin(), password, user.getAuthorities());
    }

    public Optional<User> updateById(Long id, ChangeUserRequest user) {
        String password = Optional.ofNullable(user.getPassword()).map(passwordEncoder::encode).orElse(null);
        return userRepository.updateById(id, user.getLogin(), password, user.getAuthorities());
    }

    public Optional<User> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

}
