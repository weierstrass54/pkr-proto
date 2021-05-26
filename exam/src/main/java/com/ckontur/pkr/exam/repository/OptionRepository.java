package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.utils.Pair;
import com.ckontur.pkr.exam.model.Option;
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
public class OptionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<Option> findAllByQuestionId(Long questionId) {
        return jdbcTemplate.getJdbcTemplate().query(
            "SELECT * FROM options WHERE question_id = ?", OptionMapper.INSTANCE, questionId
        );
    }

    public Map<Long, List<Option>> findAllByQuestionIds(List<Long> questionIds) {
        if (questionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return jdbcTemplate.query(
            "SELECT * FROM options WHERE question_id IN (:ids)",
                new MapSqlParameterSource("ids", questionIds),
                (rs, rowNum) -> {
                    Option option = OptionMapper.INSTANCE.mapRow(rs, rowNum);
                    return new Pair<>(rs.getLong("question_id"), option);
                }
        ).stream().collect(
            Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toList()))
        );
    }

    @Transactional
    public Optional<Option> create(Long questionId, QuestionRequests.CreateOption option) {
        return jdbcTemplate.getJdbcTemplate().query(
            "INSERT INTO options(question_id, type, text) VALUES (?, ?, ?) RETURNING *",
            OptionMapper.INSTANCE, questionId, option.getType().getValue(), option.getText()
        ).stream().findAny();
    }

    @Transactional
    public List<Option> deleteByQuestionId(Long questionId) {
        return jdbcTemplate.getJdbcTemplate().query(
            "DELETE FROM options WHERE question_id = ?", OptionMapper.INSTANCE, questionId
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
