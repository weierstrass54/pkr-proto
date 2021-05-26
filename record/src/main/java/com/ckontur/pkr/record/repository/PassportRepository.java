package com.ckontur.pkr.record.repository;

import com.ckontur.pkr.record.model.Passport;
import com.ckontur.pkr.record.model.RecordUser;
import com.ckontur.pkr.record.web.PassportRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PassportRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Passport> getByUser(RecordUser user) {
        return jdbcTemplate.query("SELECT * FROM passports WHERE user_id = ?",
            PassportMapper.INSTANCE, user.getId()).stream().findAny();
    }

    public Optional<Passport> create(RecordUser user, PassportRequests.PassportCreate passport) {
        return jdbcTemplate.query("INSERT INTO passports(user_id, series, number, issued_by, issued_at) " +
            "VALUES (?, ?, ?, ?, ?)", PassportMapper.INSTANCE, user.getId(), passport.getSeries(), passport.getNumber(),
                passport.getIssuedBy(), Timestamp.valueOf(passport.getIssuedBy())).stream().findAny();
    }

    public Optional<Passport> deleteByUser(RecordUser user) {
        return jdbcTemplate.query("DELETE FROM passports WHERE user_id = ? RETURNING *",
            PassportMapper.INSTANCE, user.getId()).stream().findAny();
    }

    private static class PassportMapper implements RowMapper<Passport> {
        private static final PassportMapper INSTANCE = new PassportMapper();
        @Override
        public Passport mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Passport(
                rs.getLong("user_id"),
                rs.getInt("series"),
                rs.getInt("number"),
                rs.getString("issued_by"),
                rs.getTimestamp("issued_at").toLocalDateTime().toLocalDate()
            );
        }
    }
}
