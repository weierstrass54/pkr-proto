package com.ckontur.pkr.auth.service;

import com.ckontur.pkr.auth.component.JwtProvider;
import com.ckontur.pkr.auth.repository.UserRepository;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticateService {
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public Option<String> authenticate(String login, String password) {
        return userRepository.findByLogin(login)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()))
            .map(jwtProvider::generateToken);
    }

}
