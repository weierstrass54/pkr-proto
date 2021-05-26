package com.ckontur.pkr.crm.repository;

import com.ckontur.pkr.crm.model.Region;
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
public class RegionRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Region> getAll() {
        return jdbcTemplate.query("SELECT * FROM regions", RegionMapper.INSTANCE);
    }

    public Optional<Region> getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM regions WHERE id = ?", RegionMapper.INSTANCE, id)
            .stream().findAny();
    }

    public Optional<Region> create(String name) {
        return jdbcTemplate.query(
            "INSERT INTO regions(name) VALUES (?) RETURNING *", RegionMapper.INSTANCE, name
        ).stream().findAny();
    }

    public Optional<Region> updateById(Long id, String name) {
        return jdbcTemplate.query(
            "UPDATE regions SET name = ? WHERE id = ? RETURNING *", RegionMapper.INSTANCE, name, id
        ).stream().findAny();
    }

    public Optional<Region> deleteById(Long id) {
        return jdbcTemplate.query("DELETE FROM regions WHERE id = ? RETURNING *", RegionMapper.INSTANCE, id)
            .stream().findAny();
    }

    private static class RegionMapper implements RowMapper<Region> {
        private static final RegionMapper INSTANCE = new RegionMapper();

        @Override
        public Region mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Region(rs.getLong("id"), rs.getString("name"));
        }
    }

}
