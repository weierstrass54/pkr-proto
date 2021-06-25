package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.common.exception.CreateEntityException;
import com.ckontur.pkr.exam.model.Qualification;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

@Repository
@RequiredArgsConstructor
public class QualificationRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Qualification> findAll() {
        return jdbcTemplate.query("SELECT * FROM qualifications", QualificationMapper.INSTANCE);
    }

    public Option<Qualification> findById(Long id) {
        return Option.ofOptional(
            jdbcTemplate.query("SELECT * FROM qualifications WHERE id = ?", QualificationMapper.INSTANCE, id)
                .stream().findAny()
        );
    }

    public Try<Qualification> create(String name) {
        return Try.of(() -> jdbcTemplate.query("INSERT INTO qualifications(name) VALUES (?) RETURNING *", QualificationMapper.INSTANCE, name).stream().findAny())
            .map(Option::ofOptional)
            .flatMap(o -> Match(o).of(
                Case($Some($()), Try::success),
                Case($None(), () -> Try.failure(new CreateEntityException("Запрос создания квалификации не вернул результата.")))
            ));
    }

    public Try<Option<Qualification>> updateById(Long id, String name) {
        return Try.of(() -> jdbcTemplate.query(
            "UPDATE qualifications SET name = ? WHERE id = ? RETURNING *", QualificationMapper.INSTANCE, name, id
        ).stream().findAny()).map(Option::ofOptional);
    }

    public Option<Qualification> deleteById(Long id) {
        return Option.ofOptional(
            jdbcTemplate.query("DELETE FROM qualifications WHERE id = ? RETURNING *", QualificationMapper.INSTANCE, id)
                .stream().findAny()
        );
    }

    private static class QualificationMapper implements RowMapper<Qualification> {
        private static final QualificationMapper INSTANCE = new QualificationMapper();

        @Override
        public Qualification mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Qualification(rs.getLong("id"), rs.getString("name"));
        }
    }
}
