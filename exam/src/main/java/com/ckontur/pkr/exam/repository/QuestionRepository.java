package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.common.exception.NotImplementedYetException;
import com.ckontur.pkr.exam.model.ChoiceQuestion;
import com.ckontur.pkr.exam.model.MatchQuestion;
import com.ckontur.pkr.exam.model.Option;
import com.ckontur.pkr.exam.model.Question;
import com.ckontur.pkr.exam.web.QuestionRequests;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

@Repository
@RequiredArgsConstructor
public class QuestionRepository {
    private final NamedParameterJdbcTemplate parametrizedJdbcTemplate;
    private final OptionRepository optionRepository;
    private final AnswerRepository answerRepository;

    public io.vavr.control.Option<Question> findById(Long id) {
        List<Option> options = optionRepository.findAllByQuestionId(id);
        return io.vavr.control.Option.ofOptional(
            parametrizedJdbcTemplate.getJdbcTemplate().query("SELECT * FROM questions WHERE id = ?",
                new QuestionMapper(Map.of(id, options)), id)
            .stream().findAny()
        );
    }

    public List<Question> findAll() {
        throw new NotImplementedYetException();
    }

    public List<Question> findAllByIds(List<Long> ids) {
        Map<Long, List<Option>> options = optionRepository.findAllByQuestionIds(ids);
        return parametrizedJdbcTemplate.query(
            "SELECT * FROM questions WHERE id IN (:ids)",
                new MapSqlParameterSource("ids", ids),
                new QuestionMapper(options));
    }

    @Transactional
    public Try<Question> create(QuestionRequests.CreateSingleOrMultipleQuestion question) {
        return createQuestionAndOptions(question)
            .filter(objects -> objects._2.size() == question.getOptions().size())
            .peek(p -> answerRepository.createSingleOrMultiple(p._2, question.getAnswers()))
            .flatMap(p -> Match(findById(p._1)).of(
                Case($Some($()), Try::success),
                Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания вопроса не вернул результата.")))
            ));
    }

    @Transactional
    public Try<Question> create(QuestionRequests.CreateSequenceQuestion question) {
        return createQuestionAndOptions(question)
            .filter(p -> p._2.size() == question.getOptions().size())
            .peek(p -> answerRepository.createSequence(p._2, question.getAnswers()))
            .flatMap(p -> Match(findById(p._1)).of(
                Case($Some($()), Try::success),
                Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания вопроса не вернул результата.")))
            ));
    }

    @Transactional
    public Try<Question> create(QuestionRequests.CreateMatchQuestion question) {
        return createQuestionAndOptions(question)
            .filter(p -> p._2.size() == question.getOptions().size())
            .peek(p -> answerRepository.createMatch(p._2, question.getAnswers()))
            .flatMap(p -> Match(findById(p._1)).of(
                Case($Some($()), Try::success),
                Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания вопроса не вернул результата.")))
            ));
    }

    private Try<Tuple2<Long, List<Long>>> createQuestionAndOptions(QuestionRequests.CreateQuestion question) {
        return io.vavr.control.Option.of(
            parametrizedJdbcTemplate.getJdbcTemplate().queryForObject(
                "INSERT INTO questions(type, text) VALUES (?, ?) RETURNING id", Long.class,
                    question.getType().getValue(), question.getText()))
        .toTry(() -> new CreateEntityException("Запрос создания вопроса не вернул результата."))
        .flatMap(questionId -> Try.sequence(
                question.getOptions().stream().map(option -> optionRepository.create(questionId, option).map(Option::getId)).collect(Collectors.toList())
            ).map(optionIds -> new Tuple2<>(questionId, optionIds.toJavaList()))
        );
    }

    @Transactional
    public io.vavr.control.Option<Question> deleteById(Long id) {
        return findById(id).map(question -> {
            answerRepository.deleteByQuestionId(question.getId());
            optionRepository.deleteByQuestionId(question.getId());
            parametrizedJdbcTemplate.getJdbcTemplate().update("DELETE FROM questions WHERE id = ?", id);
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
                    id, type, rs.getString("text"), options.getOrDefault(id, Collections.emptyList())
                );
                case MATCHING -> new MatchQuestion(
                    id, type, rs.getString("text"), options.getOrDefault(id, Collections.emptyList())
                );
            };
        }
    }

}
