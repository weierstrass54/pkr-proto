package com.ckontur.pkr.crm.service;

import com.ckontur.pkr.common.model.Page;
import com.ckontur.pkr.crm.model.CrmUser;
import com.ckontur.pkr.crm.model.PageRequest;
import com.ckontur.pkr.crm.repository.AuthRepository;
import com.ckontur.pkr.crm.repository.SearchUserRepository;
import com.ckontur.pkr.crm.web.UserRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthRepository authRepository;
    private final SearchUserRepository userRepository;

    public Page<CrmUser> search(String searchString, PageRequest pageRequest) {
        return Optional.ofNullable(searchString)
            .map(s -> userRepository.findBySearchString(s, pageRequest))
            .or(() -> Optional.of(userRepository.findAll(pageRequest)))
            .orElse(Page.empty());
    }

    public Optional<CrmUser> getById(Long id) {
        return userRepository.getById(id);
    }

    public Optional<CrmUser> create(UserRequests.CreateUser userData) {
        return authRepository.create(userData)
            .flatMap(user -> userRepository.create(user, userData));
    }

    public Optional<CrmUser> updateById(Long id, UserRequests.ChangeUser userData) {
        return Optional.of(userData)
            .filter(ud -> Stream.of(ud.getLogin(), ud.getPassword(), ud.getAuthority()).anyMatch(Objects::nonNull))
            .flatMap(ud -> authRepository.updateById(id, ud))
            .or(() -> authRepository.getById(id))
            .flatMap(user -> userRepository.update(user, userData));
    }

    public Optional<CrmUser> deleteById(Long id) {
        return authRepository.deleteById(id)
            .flatMap(userRepository::delete);
    }
}
