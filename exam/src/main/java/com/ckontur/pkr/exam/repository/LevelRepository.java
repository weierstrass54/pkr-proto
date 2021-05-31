package com.ckontur.pkr.exam.repository;

import com.ckontur.pkr.core.model.Level;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LevelRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Level> getAll() {
        return jdbcTemplate.query("SELECT * FROM levels", LevelMapper.INSTANCE);
    }

    public Optional<Level> getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM levels WHERE id = ?", LevelMapper.INSTANCE, id).stream().findAny();
    }

    public Optional<Level> create(String name) {
        return jdbcTemplate.query(
            "INSERT INTO levels(name) VALUES (?) RETURNING *", LevelMapper.INSTANCE, name
        ).stream().findAny();
    }

    public Optional<Level> updateById(Long id, String name) {
        return jdbcTemplate.query(
            "UPDATE levels SET name = ? WHERE id = ? RETURNING *", LevelMapper.INSTANCE, name, id
        ).stream().findAny();
    }

    public Optional<Level> deleteById(Long id) {
        return jdbcTemplate.query("DELETE FROM levels WHERE id = ? RETURNING *", LevelMapper.INSTANCE, id)
            .stream().findAny();
    }

    private static class LevelMapper implements RowMapper<Level> {
        private static final LevelMapper INSTANCE = new LevelMapper();

        @Override
        public Level mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Level(rs.getLong("id"), rs.getString("name"));
        }
    }

}
