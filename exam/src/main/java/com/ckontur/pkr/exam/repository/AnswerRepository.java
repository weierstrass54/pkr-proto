package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.exam.model.question.Answer;
import com.ckontur.pkr.exam.model.question.ChoiceAnswer;
import com.ckontur.pkr.exam.model.question.MatchAnswer;
import com.ckontur.pkr.exam.model.question.Option;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
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
    public List<Answer> findAllByQuestionId(Long questionId) {
        final String queryChoices = "SELECT option_id FROM (" +
                "SELECT option_id, 0 AS ordinal " +
                "FROM options o " +
                "JOIN answers_list al ON al.option_id = o.id " +
                "WHERE o.question_id = :id " +
                "UNION " +
                "SELECT option_id, as.ordinal " +
                "FROM options o " +
                "JOIN answers_sequence as ON as.option_id = o.id" +
                "WHERE o.question_id = :id" +
            ") t " +
            "ORDER BY t.ordinal";
        List<Answer> choices = List.ofAll(
            parametrizedJdbcTemplate.query(queryChoices,
                new MapSqlParameterSource("id", questionId),
                    (rs, rowNum) -> new ChoiceAnswer(rs.getLong("option_id")))
        );

        final String queryMatches = "SELECT " +
            "am.option_id_left, am.option_id_right " +
            "FROM options o " +
            "JOIN answers_matches am ON am.option_id_left = o.id " +
            "WHERE o.question_id = ? AND o.type = ?";
        List<Answer> matches = List.ofAll(
            parametrizedJdbcTemplate.getJdbcTemplate().query(queryMatches,
                (rs, rowNum) -> new MatchAnswer(rs.getLong(1), rs.getLong(2)),
                questionId, Option.Type.LEFT.getValue())
        );
        return choices.appendAll(matches);
    }

    @Transactional
    public Map<Long, List<Answer>> findAllByExamIdGroupedByQuestionId(Long examId) {
        final String queryChoices = "SELECT " +
            "id, option_id FROM ( " +
                "SELECT q.id, al.option_id, 0 AS ordinal " +
                "FROM exam_questions eq " +
                "JOIN questions q ON q.id = eq.question_id " +
                "JOIN options o ON o.question_id = q.id " +
                "JOIN answers_list al ON al.option_id = o.id " +
                "WHERE eq.exam_id = :examId " +
                "UNION " +
                "SELECT q.id, as.option_id, as.ordinal " +
                "FROM exam_questions eq " +
                "JOIN questions q ON q.id = eq.question_id " +
                "JOIN options o ON o.question_id = q.id " +
                "JOIN answers_sequence as ON as.option_id = o.id " +
                "WHERE eq.exam_id = :examId " +
            ") t " +
            "ORDER BY t.ordinal";
        List<Tuple2<Long, Answer>> choiceAnswers = List.ofAll(
            parametrizedJdbcTemplate.query(queryChoices,
                new MapSqlParameterSource("examId", examId),
                    (rs, rowNum) -> new Tuple2<>(rs.getLong(1), new ChoiceAnswer(rs.getLong(2)))
            )
        );

        final String queryMatches = "SELECT " +
            "q.id, am.option_id_left, am.option_id_right " +
            "FROM exam_questions eq " +
            "JOIN questions q ON eq.question_id = q.id " +
            "JOIN options o ON o.question_id = q.id " +
            "JOIN answers_matches am ON am.option_id_left = o.id AND o.type = ? " +
            "WHERE eq.exam_id = ?";
        List<Tuple2<Long, Answer>> matchAnswers = List.ofAll(
            parametrizedJdbcTemplate.getJdbcTemplate().query(queryMatches,
                (rs, rowNum) -> new Tuple2<>(rs.getLong(1), new MatchAnswer(rs.getLong(2), rs.getLong(3))),
                    Option.Type.LEFT.getValue(), examId)
        );
        return choiceAnswers.appendAll(matchAnswers).groupBy(Tuple2::_1).mapValues(__ -> __.map(Tuple2::_2));
    }

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
