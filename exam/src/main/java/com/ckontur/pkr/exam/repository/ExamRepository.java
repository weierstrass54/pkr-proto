package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.utils.Try;
import com.ckontur.pkr.exam.model.Exam;
import com.ckontur.pkr.exam.model.Question;
import com.ckontur.pkr.exam.web.ExamRequests;
import lombok.RequiredArgsConstructor;
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
        final String query = "SELECT " +
                "e.id, q.name AS qualification, l.name AS level, e.duration, e.points_per_correct, e.percent_passed, " +
                "e.skippable, e.previousable, e.is_published " +
            "FROM exams e " +
            "JOIN qualifications q ON e.qualification_id = q.id " +
            "JOIN levels l ON e.level_id = l.id" +
            "WHERE e.id = ?";
        return jdbcTemplate.query(query, new ExamMapper(questions), id)
            .stream().findAny();
    }

    @Transactional
    public Try<Optional<Exam>> create(ExamRequests.CreateExam exam) {
        final String query = "INSERT INTO exams(qualification_id, level_id, duration, points_per_correct, " +
            "percent_passed, skippable, previousable, is_published) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        return Try.of(() -> jdbcTemplate.queryForObject(query, Long.class, exam.getQualificationId(), exam.getLevelId(),
            exam.getDuration().toMinutes(), exam.getPointsPerCorrect(), exam.getPercentPassed(), exam.isSkippable(),
            exam.isPreviousable(), exam.isPublished()
        )).map(this::findById);
    }

    @Transactional
    public Try<Optional<Exam>> updateById(Long id, ExamRequests.ChangeExam exam) {
        return Try.of(() ->
            jdbcTemplate.update("UPDATE exams SET " +
                "qualification_id = COALESCE(?, qualification_id), " +
                "level_id = COALESCE(?, level_id), " +
                "duration = COALESCE(?, duration), " +
                "points_per_correct = COALESCE(?, points_per_correct), " +
                "percent_passed = COALESCE(?, percent_passed), " +
                "skippable = COALESCE(?, skippable), " +
                "previousable = COALESCE(?, previousable), " +
                "is_published = COALESCE(?, is_published) " +
                "WHERE id = ?", exam.getQualificationId(), exam.getLevelId(),
            exam.getDuration().map(Duration::toMinutes).orElse(null), exam.getPointsPerCorrect(),
            exam.getPercentPassed(), exam.getSkippable(), exam.getPreviousable(), exam.getIsPublished(), id)
        ).map(__ -> findById(id));
    }

    @Transactional
    public Try<Exam> addQuestionsByIds(Long id, List<Long> questionIds) {
        final String query = "INSERT INTO exam_questions(exam_id, question_id) VALUES (?, ?)";
        return Try.of(
            questionIds.stream().map(questionId ->
                Try.of(() -> jdbcTemplate.update(query, id, questionId))
            ).collect(Collectors.toList())
        ).map(__ -> findById(id).get());
    }

    @Transactional
    public Optional<Exam> removeQuestionsByIds(Long id, List<Long> questionIds) {
        questionIds.forEach(questionId ->
            jdbcTemplate.update("DELETE FROM exam_questions WHERE exam_id = ? AND question_id = ?", id, questionId)
        );
        return findById(id);
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
                rs.getInt("id"),
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
