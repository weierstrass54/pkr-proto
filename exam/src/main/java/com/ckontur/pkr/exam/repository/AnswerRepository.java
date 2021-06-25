package com.ckontur.pkr.exam.repository;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AnswerRepository {
    private final NamedParameterJdbcTemplate parametrizedJdbcTemplate;

    @Transactional
    public Try<List<Long>> createSingleOrMultiple(List<Long> optionIds, List<Integer> answers) {
        return Try.sequence(
            answers.map(i -> Try.of(() ->
                parametrizedJdbcTemplate.getJdbcTemplate().update(
                    "INSERT INTO answers_list(option_id) VALUES (?)", optionIds.get(i)
                )
            ))
        ).map(__ -> optionIds);
    }

    @Transactional
    public Try<List<Long>> createSequence(List<Long> optionIds, List<Integer> answers) {
        return Try.sequence(
            List.rangeClosed(1, optionIds.size()).map(i -> Try.of(() ->
                parametrizedJdbcTemplate.getJdbcTemplate().update(
                    "INSERT INTO answers_sequence(option_id, ordinal) VALUES (?, ?)",
                        optionIds.get(answers.get(i)), i
                )
            ))
        ).map(__ -> optionIds);
    }

    @Transactional
    public Try<Map<Long, Long>> createMatch(List<Long> optionIds, Map<Integer, Integer> answers) {
        return Try.sequence(
            answers.toList().map(t -> Try.of(() -> {
                parametrizedJdbcTemplate.getJdbcTemplate().update(
                    "INSERT INTO answers_matches(option_id_left, option_id_right) VALUES (?, ?)",
                        optionIds.get(t._1), optionIds.get(t._2)
                );
                return new Tuple2<>(optionIds.get(t._1), optionIds.get(t._2));
            }))
        ).map(v -> v.collect(HashMap.collector()));
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
