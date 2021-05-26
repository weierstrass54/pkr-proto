package com.ckontur.pkr.record.repository;

import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.record.model.RecordUser;
import com.ckontur.pkr.record.web.UserRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<RecordUser> getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", RecordUserMapper.INSTANCE, id)
            .stream().findAny();
    }

    public Optional<RecordUser> create(User user, UserRequests.CreateUser userData) {
        return jdbcTemplate.query("INSERT INTO users(id, login, first_name, middle_name, last_name, phone, email," +
            " employer, appointment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING *", RecordUserMapper.INSTANCE,
                user.getId()).stream().findAny();
    }

    public Optional<RecordUser> update(User user, UserRequests.ChangeUser userData) {
        return jdbcTemplate.query("UPDATE users SET " +
                "login = COALESCE(?, login), " +
                "first_name = COALESCE(?, first_name), " +
                "middle_name = COALESCE(?, middle_name), " +
                "last_name = COALESCE(?, last_name), " +
                "phone = COALESCE(?, phone), " +
                "email = COALESCE(?, email), " +
                "employer = COALESCE(?, employer), " +
                "appointment = COALESCE(?, appointment) " +
            "WHERE id = ?", RecordUserMapper.INSTANCE, user.getLogin(), userData.getFirstName(),
            userData.getMiddleName(), userData.getLastName(), userData.getPhone(), userData.getEmail(),
            userData.getEmployer(), userData.getAppointment()).stream().findAny();
    }

    public Optional<RecordUser> delete(User user) {
        return jdbcTemplate.query("DELETE FROM users WHERE user_id = ? RETURNING *", RecordUserMapper.INSTANCE,
            user.getId()).stream().findAny();
    }

    private static class RecordUserMapper implements RowMapper<RecordUser> {
        private static final RecordUserMapper INSTANCE = new RecordUserMapper();

        @Override
        public RecordUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new RecordUser(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("first_name"),
                rs.getString("middle_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("employer"),
                rs.getString("appointment")
            );
        }
    }
}
