package com.ckontur.pkr.crm.repository;

import com.ckontur.pkr.common.model.Page;
import com.ckontur.pkr.crm.model.CrmUser;
import com.ckontur.pkr.crm.model.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Repository
public class SearchUserRepository extends UserRepository {
    @Autowired
    public SearchUserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Transactional
    public Page<CrmUser> findBySearchString(String searchString, PageRequest pageRequest) {
        MapSqlParameterSource params = new MapSqlParameterSource(
            "search", "%" + searchString.toLowerCase() + "%"
        );
        Long total = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE " + whereExpression(), params, Long.class);
        List<CrmUser> users = jdbcTemplate.query("SELECT * FROM users WHERE "
            + whereExpression() + orderByOffsetLimitExpression(pageRequest), params, CrmUserMapper.INSTANCE);
        return Page.of(users, pageRequest.getSize(), total);
    }

    private String whereExpression() {
        return Stream.of("login", "first_name", "middle_name", "last_name", "phone", "email")
            .map(field -> "LOWER(" + field + ") LIKE :search")
            .collect(Collectors.joining(" OR "));
    }

    private String orderByOffsetLimitExpression(PageRequest pageRequest) {
        return String.format(" ORDER BY last_name %1$s, middle_name %1$s, first_name %1$s " +
            "OFFSET %2$d LIMIT %3$d", pageRequest.getDirection().name(), pageRequest.getOffset(), pageRequest.getSize());
    }
}
