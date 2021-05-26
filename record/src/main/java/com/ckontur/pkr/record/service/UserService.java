package com.ckontur.pkr.record.service;

import com.ckontur.pkr.record.model.RecordUser;
import com.ckontur.pkr.record.repository.CrmRepository;
import com.ckontur.pkr.record.repository.UserRepository;
import com.ckontur.pkr.record.web.UserRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final CrmRepository crmRepository;
    private final UserRepository userRepository;

    public Optional<RecordUser> getById(Long id) {
        return userRepository.getById(id);
    }

    public Optional<RecordUser> create(UserRequests.CreateUser userData) {
        return crmRepository.create(userData)
            .flatMap(user -> userRepository.create(user, userData));
    }

    public Optional<RecordUser> updateById(Long id, UserRequests.ChangeUser userData) {
        return Optional.of(userData)
            .filter(ud -> Stream.of(
                userData.getLogin(), userData.getFirstName(), userData.getMiddleName(), userData.getLastName(),
                userData.getPhone(), userData.getEmail()
            ).anyMatch(Objects::nonNull))
            .flatMap(ud -> crmRepository.updateUserById(id, ud))
            .or(() -> crmRepository.getUserById(id))
            .flatMap(user -> userRepository.update(user, userData));
    }

    public Optional<RecordUser> deleteById(Long id) {
        return crmRepository.deleteUserById(id)
            .flatMap(userRepository::delete);
    }

}
