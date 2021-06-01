package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.exception.NotImplementedYetException;
import com.ckontur.pkr.common.utils.Pair;
import com.ckontur.pkr.exam.model.ChoiceQuestion;
import com.ckontur.pkr.exam.model.MatchQuestion;
import com.ckontur.pkr.exam.model.Option;
import com.ckontur.pkr.exam.model.Question;
import com.ckontur.pkr.exam.web.QuestionRequests;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QuestionRepository {
    private final NamedParameterJdbcTemplate parametrizedJdbcTemplate;
    private final OptionRepository optionRepository;
    private final AnswerRepository answerRepository;

    public Optional<Question> findById(Long id) {
        List<Option> options = optionRepository.findAllByQuestionId(id);
        return parametrizedJdbcTemplate.getJdbcTemplate().query("SELECT * FROM questions WHERE id = ?",
            new QuestionMapper(Map.of(id, options)), id)
            .stream().findAny();
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
    public Optional<Question> create(QuestionRequests.CreateSingleOrMultipleQuestion question) {
        return createQuestionAndOptions(question)
            .filter(p -> p.getRight().size() == question.getOptions().size())
            .flatMap(p -> {
                answerRepository.createSingleOrMultiple(p.getRight(), question.getAnswers());
                return findById(p.getLeft());
            });
    }

    @Transactional
    public Optional<Question> create(QuestionRequests.CreateSequenceQuestion question) {
        return createQuestionAndOptions(question)
            .filter(p -> p.getRight().size() == question.getOptions().size())
            .flatMap(p -> {
                answerRepository.createSequence(p.getRight(), question.getAnswers());
                return findById(p.getLeft());
            });
    }

    @Transactional
    public Optional<Question> create(QuestionRequests.CreateMatchQuestion question) {
        return createQuestionAndOptions(question)
            .filter(p -> p.getRight().size() == question.getOptions().size())
            .flatMap(p -> {
                answerRepository.createMatch(p.getRight(), question.getAnswers());
                return findById(p.getLeft());
            });
    }

    private Optional<Pair<Long, List<Long>>> createQuestionAndOptions(QuestionRequests.CreateQuestion question) {
        return Optional.ofNullable(
            parametrizedJdbcTemplate.getJdbcTemplate().queryForObject(
                "INSERT INTO questions(type, text) VALUES (?, ?) RETURNING id", Long.class,
                question.getType().getValue(), question.getText())
        ).map(questionId -> {
            List<Long> optionIds = question.getOptions().stream()
                .map(option -> optionRepository.create(questionId, option))
                .flatMap(Optional::stream)
                .map(Option::getId)
                .collect(Collectors.toList());
            return Pair.of(questionId, optionIds);
        });
    }

    @Transactional
    public Optional<Question> deleteById(Long id) {
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
