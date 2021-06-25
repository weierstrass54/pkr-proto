package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.exam.model.Level;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

@Repository
@RequiredArgsConstructor
public class LevelRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Level> findAll() {
        return List.ofAll(jdbcTemplate.query("SELECT * FROM levels", LevelMapper.INSTANCE));
    }

    public Option<Level> findById(Long id) {
        return Option.ofOptional(jdbcTemplate.query("SELECT * FROM levels WHERE id = ?", LevelMapper.INSTANCE, id).stream().findAny());
    }

    public Try<Level> create(String name) {
        return Try.of(() -> jdbcTemplate.query("INSERT INTO levels(name) VALUES (?) RETURNING *", LevelMapper.INSTANCE, name).stream().findAny())
            .map(Option::ofOptional)
            .flatMap(o -> Match(o).of(
                Case($Some($()), Try::success),
                Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания уровня не вернул результата.")))
            ));
    }

    public Try<Option<Level>> updateById(Long id, String name) {
        return Try.of(() -> jdbcTemplate.query(
            "UPDATE levels SET name = ? WHERE id = ? RETURNING *", LevelMapper.INSTANCE, name, id
        ).stream().findAny()).map(Option::ofOptional);
    }

    public Option<Level> deleteById(Long id) {
        return Option.ofOptional(
            jdbcTemplate.query("DELETE FROM levels WHERE id = ? RETURNING *", LevelMapper.INSTANCE, id).stream().findAny()
        );
    }

    private static class LevelMapper implements RowMapper<Level> {
        private static final LevelMapper INSTANCE = new LevelMapper();

        @Override
        public Level mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Level(rs.getLong("id"), rs.getString("name"));
        }
    }

}
