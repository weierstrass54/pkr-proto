package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.exam.model.question.Option;
import com.ckontur.pkr.exam.web.QuestionRequests;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

@Repository
@RequiredArgsConstructor
public class OptionRepository {
    private final NamedParameterJdbcTemplate parameterJdbcTemplate;

    public List<Option> findAllByQuestionId(Long questionId) {
        return List.ofAll(
            parameterJdbcTemplate.getJdbcTemplate().query(
               "SELECT * FROM options WHERE question_id = ?", OptionMapper.INSTANCE, questionId
            )
        );
    }

    public Map<Long, List<Option>> findAllByExamIdGroupedByQuestionId(Long examId) {
        final String query = "SELECT o.* " +
            "FROM exam_questions eq " +
            "JOIN questions q ON eq.question_id = q.id " +
            "JOIN options o ON o.question_id = q.id " +
            "WHERE eq.exam_id = ?";
        return List.ofAll(
            parameterJdbcTemplate.getJdbcTemplate().query(query, (rs, rowNum) -> {
                Option option = OptionMapper.INSTANCE.mapRow(rs, rowNum);
                return new Tuple2<>(rs.getLong("question_id"), option);
            }, examId)
        ).groupBy(Tuple2::_1).mapValues(__ -> __.map(Tuple2::_2));
    }

    public Map<Long, List<Option>> findAllByQuestionIds(List<Long> questionIds) {
        return io.vavr.control.Option.of(questionIds)
            .filter(qids -> !qids.isEmpty())
            .map(qids -> List.ofAll(parameterJdbcTemplate.query(
                "SELECT * FROM options WHERE question_id IN (:ids)",
                new MapSqlParameterSource("ids", questionIds),
                (rs, rowNum) -> {
                    Option option = OptionMapper.INSTANCE.mapRow(rs, rowNum);
                    return new Tuple2<>(rs.getLong("question_id"), option);
                }))
            )
            .map(v -> v.groupBy(Tuple2::_1).mapValues(__ -> __.map(Tuple2::_2)))
            .getOrElse(HashMap.empty());
    }

    public Try<Option> create(Long questionId, QuestionRequests.CreateOption option) {
        return Try.of(() ->
            parameterJdbcTemplate.getJdbcTemplate().query(
            "INSERT INTO options(question_id, type, text) VALUES (?, ?, ?) RETURNING *",
                OptionMapper.INSTANCE, questionId, option.getType().getValue(), option.getText()
            ).stream().findAny()
        )
        .map(io.vavr.control.Option::ofOptional)
        .flatMap(o -> Match(o).of(
            Case($Some($()), Try::success),
            Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания варианта ответа не вернул результата.")))
        ));
    }

    public void deleteByQuestionId(Long questionId) {
        parameterJdbcTemplate.getJdbcTemplate().query(
            "DELETE FROM options WHERE question_id = ? RETURNING *", OptionMapper.INSTANCE, questionId
        );
    }

    private static class OptionMapper implements RowMapper<Option> {
        private static final OptionMapper INSTANCE = new OptionMapper();

        @Override
        public Option mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Option(
                rs.getLong("id"),
                Option.Type.of(rs.getInt("type")),
                rs.getString("text")
            );
        }
    }
}
