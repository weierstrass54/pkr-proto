package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotImplementedYetException;
import com.ckontur.pkr.common.request.PageRequest;
import com.ckontur.pkr.exam.model.question.Option;
import com.ckontur.pkr.exam.model.question.*;
import com.ckontur.pkr.exam.web.QuestionRequests;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

@Repository
@RequiredArgsConstructor
public class QuestionRepository {
    private final JdbcTemplate jdbcTemplate;
    private final OptionRepository optionRepository;
    private final AnswerRepository answerRepository;

    public io.vavr.control.Option<Question> findById(Long id) {
        return io.vavr.control.Option.ofOptional(
            jdbcTemplate.query(
                "SELECT * FROM questions WHERE id = ?",
                (rs, rowNum) -> new Tuple3<>(
                    rs.getLong("id"), Question.Type.of(rs.getInt("type")), rs.getString("text")
                ), id
            ).stream().findAny()
        ).map(q -> {
            List<Option> options = optionRepository.findAllByQuestionId(q._1);
            return switch(q._2) {
                case SINGLE, MULTIPLE, SEQUENCE -> new AnsweredChoiceQuestion(
                    q._1, q._2, q._3, options, answerRepository.findAllByChoiceQuestionId(q._1)
                );
                case  MATCHING -> new AnsweredMatchQuestion(
                    q._1, q._2, q._3, options, answerRepository.findAllByMatchQuestionId(q._1)
                );
            };
        });
    }

    public List<Question> findAll(PageRequest pageRequest) {
        throw new NotImplementedYetException();
    }

    public List<Question> findAllByExamId(Long examId) {
        Map<Long, List<Option>> options = optionRepository.findAllByExamIdGroupedByQuestionId(examId);
        return List.ofAll(
            jdbcTemplate.query(
                "SELECT q.* " +
                "FROM exam_questions eq " +
                "JOIN questions q ON q.id = eq.question_id " +
                "WHERE eq.exam_id = ?", new QuestionMapper(options), examId
            )
        );
    }

    @Transactional
    public Try<Question> create(QuestionRequests.CreateSingleOrMultipleQuestion question) {
        return createQuestionAndOptions(question)
            .filter(objects -> objects._2.size() == question.getOptions().size())
            .flatMap(p -> answerRepository.createSingleOrMultiple(p._2, question.getAnswers()).map(__ -> p._1))
            .flatMap(id -> Match(findById(id)).of(
                Case($Some($()), Try::success),
                Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания вопроса не вернул результата.")))
            ));
    }

    @Transactional
    public Try<Question> create(QuestionRequests.CreateSequenceQuestion question) {
        return createQuestionAndOptions(question)
            .filter(p -> p._2.size() == question.getOptions().size())
            .flatMap(p -> answerRepository.createSequence(p._2, question.getAnswers()).map(__ -> p._1))
            .flatMap(id -> Match(findById(id)).of(
                Case($Some($()), Try::success),
                Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания вопроса не вернул результата.")))
            ));
    }

    @Transactional
    public Try<Question> create(QuestionRequests.CreateMatchQuestion question) {
        return createQuestionAndOptions(question)
            .filter(p -> p._2.size() == question.getOptions().size())
            .flatMap(p -> answerRepository.createMatch(p._2, question.getAnswers()).map(__ -> p._1))
            .flatMap(id -> Match(findById(id)).of(
                Case($Some($()), Try::success),
                Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания вопроса не вернул результата.")))
            ));
    }

    private Try<Tuple2<Long, List<Long>>> createQuestionAndOptions(QuestionRequests.CreateQuestion question) {
        return io.vavr.control.Option.of(
            jdbcTemplate.queryForObject(
                "INSERT INTO questions(type, text) VALUES (?, ?) RETURNING id", Long.class,
                    question.getType().getValue(), question.getText()))
        .toTry(() -> new CreateEntityException("Запрос создания вопроса не вернул результата."))
        .flatMap(questionId -> Try.sequence(
                question.getOptions().map(option -> optionRepository.create(questionId, option).map(Option::getId))
            ).map(optionIds -> new Tuple2<>(questionId, optionIds.toList()))
        );
    }

    @Transactional
    public io.vavr.control.Option<Question> deleteById(Long id) {
        return findById(id).map(question -> {
            answerRepository.deleteByQuestionId(question.getId());
            optionRepository.deleteByQuestionId(question.getId());
            jdbcTemplate.update("DELETE FROM questions WHERE id = ?", id);
            return question;
        });
    }

    @RequiredArgsConstructor
    private static class QuestionMapper implements RowMapper<Question> {
        private final Map<Long, List<Option>> options;

        @Override
        public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            Question.Type type = Question.Type.of(rs.getInt("type"));
            return switch (type) {
                case SINGLE, MULTIPLE, SEQUENCE -> new ChoiceQuestion(
                    id, type, rs.getString("text"), options.getOrElse(id, List.empty())
                );
                case MATCHING -> new MatchQuestion(
                    id, type, rs.getString("text"), options.getOrElse(id, List.empty())
                );
            };
        }
    }

}
