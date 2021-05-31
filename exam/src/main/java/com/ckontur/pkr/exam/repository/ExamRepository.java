package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.exam.model.Exam;
import com.ckontur.pkr.exam.model.Question;
import com.ckontur.pkr.exam.web.ExamRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ExamRepository {
    private final JdbcTemplate jdbcTemplate;
    private final QuestionRepository questionRepository;

    @Transactional
    public Optional<Exam> findById(Long id) {
        List<Long> questionIds = jdbcTemplate.queryForList(
            "SELECT question_id FROM exam_questions WHERE exam_id = ?", Long.class, id
        );
        List<Question> questions = questionRepository.findAllByIds(questionIds);
        final String query = "SELECT * FROM exams WHERE id = ?";
        return jdbcTemplate.query(query, new ExamMapper(questions), id)
            .stream().findAny();
    }

    @Transactional
    public Optional<Exam> create(ExamRequests.CreateExam exam) {
        final String query = "INSERT INTO exams(qualification_id, level_id, duration, points_per_correct, " +
            "percent_passed, skippable, previousable, is_published) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(query, Long.class, exam.getQualificationId(), exam.getLevelId(),
            exam.getDuration().toMinutes(), exam.getPointsPerCorrect(), exam.getPercentPassed(), exam.isSkippable(),
            exam.isPreviousable(), exam.isPublished()
        );
        return findById(id);
    }

    @Transactional
    public Optional<Exam> updateById(Long id, ExamRequests.ChangeExam exam) {
        boolean exists = jdbcTemplate.update("UPDATE exams SET " +
                "duration = COALESCE(?, duration), " +
                "points_per_correct = COALESCE(?, points_per_correct), " +
                "percent_passed = COALESCE(?, percent_passed), " +
                "skippable = COALESCE(?, skippable), " +
                "previousable = COALESCE(?, previousable), " +
                "is_published = COALESCE(?, is_published) " +
            "WHERE id = ?") > 0;
        return findById(id);
    }

    @Transactional
    public Optional<Exam> addQuestionsByIds(Long id, List<Long> questionIds) {
        try {
            questionIds.forEach(questionId -> jdbcTemplate.update("INSERT INTO "));
        }
        catch (DataIntegrityViolationException e) {

        }
    }

    @Transactional
    public Optional<Exam> deleteById(Long id) {
        return findById(id).map(exam -> {
            jdbcTemplate.update("DELETE FROM exam_questions WHERE exam_id = ?", id);
            jdbcTemplate.update("DELETE FROM exams WHERE id = ?", id);
            return exam;
        });
    }

    @RequiredArgsConstructor
    private static class ExamMapper implements RowMapper<Exam> {
        private final List<Question> questions;

        @Override
        public Exam mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Exam(
                rs.getString("qualification"),
                rs.getString("level"),
                Duration.ofMinutes(rs.getLong("duration")),
                rs.getInt("points_per_correct"),
                rs.getInt("percent_passed"),
                rs.getBoolean("skippable"),
                rs.getBoolean("previousable"),
                rs.getBoolean("is_published"),
                questions
            );
        }
    }
}
