package com.ckontur.pkr.crm.repository;

import com.ckontur.pkr.crm.model.Assessment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AssessmentRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Assessment> getAll() {
        final String query = "SELECT a.id, a.name, r.name AS region, a.address, a.capacity " +
            "FROM assessments a " +
            "JOIN regions r ON a.region_id = r.id";
        return jdbcTemplate.query(query, AssessmentMapper.INSTANCE);
    }

    public List<Assessment> getAllByRegionId(Long regionId) {
        final String query = "SELECT a.id, a.name, r.name AS region, a.address, a.capacity " +
            "FROM assessments a " +
            "JOIN regions r ON a.region_id = r.id " +
            "WHERE a.region_id = ?";
        return jdbcTemplate.query(query, AssessmentMapper.INSTANCE, regionId);
    }

    public Optional<Assessment> getById(Long id) {
        final String query = "SELECT a.id, a.name, r.name AS region, a.address, a.capacity " +
            "FROM assessments a " +
            "JOIN regions r ON a.region_id = r.id " +
            "WHERE a.id = ?";
        return jdbcTemplate.query(query, AssessmentMapper.INSTANCE, id)
            .stream().findAny();
    }

    public Optional<Assessment> create(String name, Long regionId, String address, Integer capacity) {
        final String query = "INSERT INTO assessments(name, region_id, address, capacity) VALUES (?, ?, ?, ?)";
        Long id = jdbcTemplate.queryForObject(query, Long.class, name, regionId, address, capacity);
        return getById(id);
    }

    public Optional<Assessment> updateById(Long id, String name, Long regionId, String address, Integer capacity) {
        jdbcTemplate.update("UPDATE assessments SET " +
            "name = COALESCE(?, name) " +
            "region_id = COALESCE(?, region_id), " +
            "address = COALESCE(?, address), " +
            "capacity = COALESCE(?, capacity)" +
            "WHERE id = ?", name, regionId, address, capacity, id);
        return getById(id);
    }

    @Transactional
    public Optional<Assessment> deleteById(Long id) {
        return getById(id)
            .filter(__ -> jdbcTemplate.update("DELETE FROM assessments WHERE id = ?", id) > 0);
    }

    private static class AssessmentMapper implements RowMapper<Assessment> {
        private static final AssessmentMapper INSTANCE = new AssessmentMapper();

        @Override
        public Assessment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Assessment(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("region"),
                rs.getString("address"),
                rs.getInt("capacity")
            );
        }
    }
}
