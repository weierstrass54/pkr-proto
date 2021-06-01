package com.ckontur.pkr.exam.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class AnswerRepository {
    private final NamedParameterJdbcTemplate parametrizedJdbcTemplate;

    @Transactional
    public void createSingleOrMultiple(List<Long> optionIds, List<Integer> answers) {
        answers.forEach(i ->
            parametrizedJdbcTemplate.getJdbcTemplate().update(
                "INSERT INTO answers_list(option_id) VALUES (?)", optionIds.get(i)
            )
        );
    }

    @Transactional
    public void createSequence(List<Long> optionIds, List<Integer> answers) {
        IntStream.rangeClosed(1, optionIds.size())
            .forEach(ordinal ->
                parametrizedJdbcTemplate.getJdbcTemplate().update(
                    "INSERT INTO answers_sequence(option_id, ordinal) VALUES (?, ?)",
                        optionIds.get(answers.get(ordinal)), ordinal
                )
            );
    }

    @Transactional
    public void createMatch(List<Long> optionIds, Map<Integer, Integer> answers) {
        answers.forEach((left, right) ->
            parametrizedJdbcTemplate.getJdbcTemplate().update(
                "INSERT INTO answers_matches(option_id_left, option_id_right) VALUES (?, ?)",
                    optionIds.get(left), optionIds.get(right)
            )
        );
    }

    @Transactional
    public void deleteByQuestionId(Long id) {
        parametrizedJdbcTemplate.getJdbcTemplate().update(
            "DELETE FROM answers_list WHERE option_id IN (SELECT id FROM options WHERE question_id = ?)", id
        );
        parametrizedJdbcTemplate.getJdbcTemplate().update(
            "DELETE FROM answers_sequence WHERE option_id IN (SELECT id FROM options WHERE question_id = ?)", id
        );
        parametrizedJdbcTemplate.update(
            "DELETE FROM answers_matches WHERE " +
                "option_id_left IN (SELECT id FROM options WHERE question_id = :id) OR " +
                "option_id_right IN (SELECT id FROM options WHERE question_id = :id)",
            new MapSqlParameterSource("id", id)
        );
    }
}
