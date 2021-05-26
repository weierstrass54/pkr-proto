package com.ckontur.pkr.crm.repository;

import com.ckontur.pkr.common.model.User;
import com.ckontur.pkr.crm.model.Passport;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PassportRepository {
    private final JdbcTemplate jdbcTemplate;

    public Optional<Passport> create(User user, Integer series, Integer number, String issuedBy, LocalDate issuedAt) {
        final String query = "INSERT INTO passports(user_id, series, number, issued_by, issued_at) " +
            "VALUES (?, ?, ?, ?, ?) RETURNING *";
        return jdbcTemplate.query(query, PassportMapper.INSTANCE, user.getId(), series, number, issuedBy,
            Timestamp.valueOf(issuedAt.atStartOfDay())).stream().findAny();
    }

    public Optional<Passport> change(Long userId, Integer series, Integer number, String issuedBy, LocalDate issuedAt) {
        final String query = "UPDATE passports SET " +
            "series = COALESCE(?, series), " +
            "number = COALESCE(?, number), " +
            "issued_by = COALESCE(?, issued_by), " +
            "issued_at = COALESCE(?, issued_at) " +
            "WHERE user_id = ? RETURNING *";
        return jdbcTemplate.query(query, PassportMapper.INSTANCE, series, number, issuedBy,
            Timestamp.valueOf(issuedAt.atStartOfDay()), userId).stream().findAny();
    }

    public Optional<Passport> deleteByUserId(Long userId) {
        final String query = "DELETE FROM passports WHERE user_id = ? RETURNING *";
        return jdbcTemplate.query(query, PassportMapper.INSTANCE, userId).stream().findAny();
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
