package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.exam.model.Exam;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExamRepository {
    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    protected final QuestionRepository questionRepository;

    public List<Exam> findAll() {
        return namedParameterJdbcTemplate.getJdbcTemplate().query("SELECT * FROM exams", ExamMapper.INSTANCE);
    }

    public List<Exam> findAllByQualificationAndLevel(Long qualificationId, Long levelId) {
        return namedParameterJdbcTemplate.getJdbcTemplate().query("SELECT * FROM exams WHERE qualification_id = ? AND level_id = ?",
            ExamMapper.INSTANCE, qualificationId, levelId);
    }

    private static class ExamMapper implements RowMapper<Exam> {
        private static final ExamMapper INSTANCE = new ExamMapper();

        @Override
        public Exam mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Exam(
                rs.getInt("id"),
                rs.getString("qualification"),
                rs.getString("level"),
                Duration.ofMinutes(rs.getLong("duration")),
                rs.getInt("points_per_correct"),
                rs.getInt("percent_passed"),
                rs.getBoolean("skippable"),
                rs.getBoolean("previousable"),
                rs.getBoolean("is_published")
            );
        }
    }
}
