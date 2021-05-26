package com.ckontur.pkr.core.repository;

import com.ckontur.pkr.core.model.Qualification;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QualificationRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Qualification> getAll() {
        return jdbcTemplate.query("SELECT * FROM qualifications", QualificationMapper.INSTANCE);
    }

    public Optional<Qualification> getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM qualifications WHERE id = ?", QualificationMapper.INSTANCE, id)
            .stream().findAny();
    }

    public Optional<Qualification> create(String name) {
        return jdbcTemplate.query(
            "INSERT INTO qualifications(name) VALUES (?) RETURNING *", QualificationMapper.INSTANCE, name
        ).stream().findAny();
    }

    public Optional<Qualification> updateById(Long id, String name) {
        return jdbcTemplate.query(
            "UPDATE qualifications SET name = ? WHERE id = ? RETURNING *", QualificationMapper.INSTANCE, name, id
        ).stream().findAny();
    }

    public Optional<Qualification> deleteById(Long id) {
        return jdbcTemplate.query("DELETE FROM qualifications WHERE id = ?", QualificationMapper.INSTANCE, id)
            .stream().findAny();
    }

    private static class QualificationMapper implements RowMapper<Qualification> {
        private static final QualificationMapper INSTANCE = new QualificationMapper();

        @Override
        public Qualification mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Qualification(rs.getLong("id"), rs.getString("name"));
        }
    }
}
