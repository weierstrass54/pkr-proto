package com.ckontur.pkr.auth.repository;

import com.ckontur.pkr.common.model.Authority;
import com.ckontur.pkr.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

import static com.ckontur.pkr.common.utils.SqlUtils.array;
import static com.ckontur.pkr.common.utils.SqlUtils.setOf;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<User> findByLogin(String login) {
        final String query = "SELECT u.id, u.login, u.password, u.roles " +
            "FROM users u " +
            "WHERE u.login = ?";
        return jdbcTemplate.query(query, UserMapper.INSTANCE, login).stream().findAny();
    }

    public Optional<User> getById(Long id) {
        final String query = "SELECT u.id, u.login, u.password, u.roles " +
            "FROM users u " +
            "WHERE u.id = ?";
        return jdbcTemplate.query(query, UserMapper.INSTANCE, id).stream().findAny();
    }

    @Transactional
    public Optional<User> create(String login, String password, Set<Authority> authorities) {
        final String query = "INSERT INTO users(login, password, roles) " +
                "VALUES (?, ?, ?::text[]) RETURNING *";
        return jdbcTemplate.query(query, UserMapper.INSTANCE, login, password, array(authorities, Authority::name))
            .stream().findAny();
    }

    public Optional<User> updateById(Long id, String login, String password, Set<Authority> authorities) {
        jdbcTemplate.update("UPDATE users SET " +
            "login = COALESCE(?, login), " +
            "password = COALESCE(?, password), " +
            "roles = COALESCE(?::text[], roles) " +
            "WHERE id = ?", login, password, array(authorities, Authority::name), id);
        return getById(id);
    }

    public Optional<User> deleteById(Long id) {
        return jdbcTemplate.query("DELETE FROM users WHERE id = ? RETURNING *", UserMapper.INSTANCE, id)
            .stream().findAny();
    }

    private static class UserMapper implements RowMapper<User> {
        private static final UserMapper INSTANCE = new UserMapper();
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("password"),
                setOf(rs.getArray("roles"), Authority::of)
            );
        }
    }

}