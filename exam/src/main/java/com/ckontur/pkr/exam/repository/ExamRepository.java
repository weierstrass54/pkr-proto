package com.ckontur.pkr.exam.repository;

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
        return jdbcTemplate.query("SELECT * FROM exams WHERE id = ?", new ExamMapper(questions), id)
            .stream().findAny();
    }

    @Transactional
    public Optional<Exam> create(ExamRequests.CreateExam exam) {
        List<Question> questions = fetchQuestions(exam.getQuestionIds());
        Long id = jdbcTemplate.queryForObject(
            "INSERT INTO exams(duration, points_per_correct, percent_passed, skippable, previousable) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id", Long.class, exam.getDuration().toMinutes(),
                        exam.getPointsPerCorrect(), exam.getPercentPassed(), exam.isSkippable(), exam.isPreviousable());
        questions.stream().map(Question::getId).forEach(
            questionId -> jdbcTemplate.update(
                "INSERT INTO exam_questions(exam_id, question_id) VALUES (?, ?)", id, questionId
            )
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
                "previousable = COALESCE(?, previousable) " +
            "WHERE id = ?") > 0;
        if (exists && !exam.getQuestionIds().isEmpty()) {
            List<Question> questions = fetchQuestions(exam.getQuestionIds());
            jdbcTemplate.update("DELETE FROM exam_questions WHERE question_id = ?", id);
            questions.stream().map(Question::getId).forEach(
                questionId -> jdbcTemplate.update(
                    "INSERT INTO exam_questions(exam_id, question_id) VALUES (?, ?)", id, questionId
                )
            );
        }
        return findById(id);
    }

    @Transactional
    public Optional<Exam> deleteById(Long id) {
        return findById(id).map(exam -> {
            jdbcTemplate.update("DELETE FROM exam_questions WHERE question_id = ?", id);
            jdbcTemplate.update("DELETE FROM exams WHERE id = ?", id);
            return exam;
        });
    }

    private List<Question> fetchQuestions(List<Long> questionIds) {
        List<Question> questions = questionRepository.findAllByIds(questionIds);
        if (questions.size() != questionIds.size()) {
            List<Long> existsQuestions = questions.stream().map(Question::getId).collect(Collectors.toList());
            String nonExistsQuestions = questionIds.stream()
                .filter(i -> !existsQuestions.contains(i))
                .map(String::valueOf).collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Вопросов " + nonExistsQuestions + " не существует.");
        }
        return questions;
    }

    @RequiredArgsConstructor
    private static class ExamMapper implements RowMapper<Exam> {
        private final List<Question> questions;

        @Override
        public Exam mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Exam(
                "q",
                "l",
                Duration.ofMinutes(rs.getLong("duration")),
                rs.getInt("points_per_correct"),
                rs.getInt("percent_passed"),
                rs.getBoolean("skippable"),
                rs.getBoolean("previousable"),
                questions
            );
        }
    }
}
