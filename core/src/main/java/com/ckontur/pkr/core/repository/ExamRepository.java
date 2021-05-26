package com.ckontur.pkr.core.repository;

import com.ckontur.pkr.core.model.Exam;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExamRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Exam> getAll() {
        final String query = "SELECT e.id, q.name AS qualification, l.name AS level, e.name " +
            "FROM exams e " +
            "JOIN qualifications q ON e.qualification_id = q.id " +
            "JOIN levels l ON e.level_id = l.id";
        return jdbcTemplate.query(query, ExamMapper.INSTANCE);
    }

    public Optional<Exam> create() {
        return Optional.empty();
    }

    @Transactional
    public Optional<Exam> updateById(Long id) {
        final String query = "";
        return Optional.of(jdbcTemplate.update(query, id))
            .filter(i -> i > 0)
            .flatMap(__ -> getById(id));
    }

    @Transactional
    public Optional<Exam> deleteById(Long id) {
        return getById(id)
            .stream().peek(e -> jdbcTemplate.update("DELETE FROM exams WHERE id = ?", e.getId()))
            .findAny();
    }

    private Optional<Exam> getById(Long id) {
        final String query = "SELECT e.id, q.name AS qualification, l.name AS level, e.name " +
            "FROM exams e " +
            "JOIN qualifications q ON e.qualification_id = q.id " +
            "JOIN levels l ON e.level_id = l.id " +
            "WHERE e.id = ?";
        return jdbcTemplate.query(query, ExamMapper.INSTANCE, id).stream().findAny();
    }

    private static class ExamMapper implements RowMapper<Exam> {
        private static final ExamMapper INSTANCE = new ExamMapper();
        @Override
        public Exam mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Exam(
                rs.getLong("id"), rs.getString("qualification"), rs.getString("level"),
                rs.getString("name")
            );
        }
    }
}
