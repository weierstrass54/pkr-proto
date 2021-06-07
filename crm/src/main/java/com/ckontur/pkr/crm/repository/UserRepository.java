package com.ckontur.pkr.crm.repository;

import com.ckontur.pkr.common.model.Page;
import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.crm.model.CrmUser;
import com.ckontur.pkr.crm.model.PageRequest;
import com.ckontur.pkr.crm.web.UserRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    protected final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public Page<CrmUser> findAll(PageRequest pageRequest) {
        final Long total = jdbcTemplate.getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM users", Long.class);
        final String query = String.format("SELECT * FROM users " +
            "ORDER BY last_name %1$s, middle_name %1$s, first_name %1$s " +
            "OFFSET %2$d LIMIT %3$d", pageRequest.getDirection().name(), pageRequest.getOffset(), pageRequest.getSize());
        List<CrmUser> users = jdbcTemplate.query(query, CrmUserMapper.INSTANCE);
        return Page.of(users, pageRequest.getSize(), total);
    }

    public Optional<CrmUser> getById(Long id) {
        return jdbcTemplate.getJdbcTemplate().query("SELECT * FROM users WHERE id = ?", CrmUserMapper.INSTANCE, id)
            .stream().findAny();
    }

    public Optional<CrmUser> create(User user, UserRequests.CreateUser userData) {
        final String query = "INSERT INTO users(" +
            "id, login, first_name, middle_name, last_name, phone, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.getJdbcTemplate().query(query, CrmUserMapper.INSTANCE, user.getId(), user.getLogin(),
            userData.getFirstName(), userData.getMiddleName(), userData.getLastName(), userData.getPhone(),
                userData.getEmail()).stream().findAny();
    }

    public Optional<CrmUser> update(User user, UserRequests.ChangeUser userData) {
        final String query = "UPDATE users SET " +
                "login = COALESCE(?, login), " +
                "first_name = COALESCE(?, first_name), " +
                "middle_name = COALESCE(?, middle_name), " +
                "last_name = COALESCE(?, last_name), " +
                "phone = COALESCE(?, phone), " +
                "email = COALESCE(?, email), " +
            "WHERE id = ? RETURNING *";
        return jdbcTemplate.getJdbcTemplate().query(query, CrmUserMapper.INSTANCE, user.getLogin(),
            userData.getFirstName(), userData.getMiddleName(), userData.getLastName(), userData.getPhone(),
                userData.getEmail()).stream().findAny();
    }

    public Optional<CrmUser> delete(User user) {
        return jdbcTemplate.getJdbcTemplate().query("DELETE FROM users WHERE id = ? RETURNING *", CrmUserMapper.INSTANCE,
            user.getId()).stream().findAny();
    }

    protected static class CrmUserMapper implements RowMapper<CrmUser> {
        protected static final CrmUserMapper INSTANCE = new CrmUserMapper();

        @Override
        public CrmUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CrmUser(
                rs.getLong("id"), rs.getString("login"), rs.getString("first_name"),
                rs.getString("middle_name"), rs.getString("last_name"),
                rs.getString("phone"), rs.getString("email")
            );
        }
    }
}
