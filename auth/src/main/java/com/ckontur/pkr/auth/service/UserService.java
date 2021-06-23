package com.ckontur.pkr.auth.service;

import com.ckontur.pkr.auth.repository.UserRepository;
import com.ckontur.pkr.auth.web.ChangeUserRequest;
import com.ckontur.pkr.auth.web.CreateUserRequest;
import com.ckontur.pkr.common.model.User;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Option<User> getById(Long id) {
        return userRepository.getById(id);
    }

    public Try<User> create(CreateUserRequest user) {
        String password = passwordEncoder.encode(user.getPassword());
        return userRepository.create(user.getLogin(), password, user.getAuthorities());
    }

    public Try<Option<User>> updateById(Long id, ChangeUserRequest user) {
        String password = Option.of(user.getPassword()).map(passwordEncoder::encode).getOrElse((String)null);
        return userRepository.updateById(id, user.getLogin(), password, user.getAuthorities());
    }

    public Option<User> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

}
