package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.exam.model.DetailedExam;
import com.ckontur.pkr.exam.model.question.Question;
import com.ckontur.pkr.exam.web.ExamRequests;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

@Repository
public class DetailedExamRepository extends ExamRepository {
    @Autowired
    public DetailedExamRepository(JdbcTemplate jdbcTemplate, QuestionRepository questionRepository) {
        super(jdbcTemplate, questionRepository);
    }

    @Transactional
    public Option<DetailedExam> findById(Long id) {
        List<Question> questions = questionRepository.findAllByExamId(id);
        final String query = "SELECT " +
            "e.id, q.name AS qualification, l.name AS level, e.duration, e.points_per_correct, e.percent_passed, " +
            "e.skippable, e.previousable, e.is_published " +
            "FROM exams e " +
            "JOIN qualifications q ON e.qualification_id = q.id " +
            "JOIN levels l ON e.level_id = l.id" +
            "WHERE e.id = ?";
        return Option.ofOptional(
            jdbcTemplate.query(query, new DetailedExamMapper(questions), id).stream().findAny()
        );
    }

    @Transactional
    public Try<DetailedExam> create(ExamRequests.CreateExam exam) {
        final String query = "INSERT INTO exams(qualification_id, level_id, duration, points_per_correct, " +
                "percent_passed, skippable, previousable, is_published) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        return Try.of(() -> jdbcTemplate.queryForObject(query, Long.class,
            exam.getQualificationId(), exam.getLevelId(), exam.getDuration().toMinutes(), exam.getPointsPerCorrect(),
            exam.getPercentPassed(), exam.isSkippable(), exam.isPreviousable(), exam.isPublished()
        )).map(this::findById)
        .flatMap(o -> Match(o).of(
            Case($Some($()), Try::success),
            Case($None(), Try.failure(new CreateEntityException("???????????? ???????????????? ???????????????? ???????????? ???????????? ??????????????????.")))
        ));
    }

    @Transactional
    public Try<Option<DetailedExam>> updateById(Long id, ExamRequests.ChangeExam exam) {
        return Try.of(() ->
            jdbcTemplate.update(
                "UPDATE exams SET " +
                    "qualification_id = COALESCE(?, qualification_id), " +
                    "level_id = COALESCE(?, level_id), " +
                    "duration = COALESCE(?, duration), " +
                    "points_per_correct = COALESCE(?, points_per_correct), " +
                    "percent_passed = COALESCE(?, percent_passed), " +
                    "skippable = COALESCE(?, skippable), " +
                    "previousable = COALESCE(?, previousable), " +
                    "is_published = COALESCE(?, is_published) " +
                "WHERE id = ?", exam.getQualificationId(), exam.getLevelId(),
                    exam.getDuration().map(Duration::toMinutes).getOrNull(), exam.getPointsPerCorrect(),
                    exam.getPercentPassed(), exam.getSkippable(), exam.getPreviousable(), exam.getIsPublished(), id)
        ).map(__ -> findById(id));
    }

    @Transactional
    public Try<DetailedExam> addQuestionsByIds(Long id, List<Long> questionIds) {
        final String query = "INSERT INTO exam_questions(exam_id, question_id) VALUES (?, ?)";
        return Try.sequence(
            questionIds.map(qId -> Try.of(() -> jdbcTemplate.update(query, id, qId)))
        ).flatMap(__ -> Match(findById(id)).of(
            Case($Some($()), Try::success),
            Case($None(), () -> Try.failure(new CreateEntityException("???????????? ?????????????????? ???????????????? ???????????? ??????????????.")))
        ));
    }

    @Transactional
    public Option<DetailedExam> removeQuestionsByIds(Long id, List<Long> questionIds) {
        jdbcTemplate.update(
            "DELETE FROM exam_questions WHERE exam_id = :exam_id AND question_id IN (:ids)",
            new MapSqlParameterSource("ids", questionIds).addValue("exam_id", id)
        );
        return findById(id);
    }

    @Transactional
    public Option<DetailedExam> deleteById(Long id) {
        return findById(id).map(detailedExam -> {
            jdbcTemplate.update("DELETE FROM exam_questions WHERE exam_id = ?", id);
            jdbcTemplate.update("DELETE FROM exams WHERE id = ?", id);
            return detailedExam;
        });
    }

    @RequiredArgsConstructor
    private static class DetailedExamMapper implements RowMapper<DetailedExam> {
        private final List<Question> questions;

        @Override
        public DetailedExam mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DetailedExam(
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
