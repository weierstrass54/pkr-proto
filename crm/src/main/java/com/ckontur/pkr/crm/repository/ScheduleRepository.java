package com.ckontur.pkr.crm.repository;

import com.ckontur.pkr.common.utils.Interval;
import com.ckontur.pkr.common.utils.SqlUtils;
import com.ckontur.pkr.crm.model.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleRepository {
    private final NamedParameterJdbcTemplate parametrizedJdbcTemplate;

    public Optional<Schedule> findById(Long id) {
        return parametrizedJdbcTemplate.getJdbcTemplate()
            .query("SELECT * FROM schedule WHERE id = ?", ScheduleMapper.INSTANCE, id).stream().findAny();
    }

    public List<Schedule> findAll() {
        return parametrizedJdbcTemplate.getJdbcTemplate().query("SELECT * FROM schedule", ScheduleMapper.INSTANCE);
    }

    public List<Schedule> findAllByAssessmentId(Long assessmentId) {
        return parametrizedJdbcTemplate.getJdbcTemplate()
            .query("SELECT * FROM schedule WHERE assessment_id = ?", ScheduleMapper.INSTANCE, assessmentId);
    }

    public List<Schedule> findAllByAssessmentIdAndExamId(Long assessmentId, Long examId) {
        return parametrizedJdbcTemplate.getJdbcTemplate().query("SELECT * FROM schedule WHERE assessment_id = ? AND exam_id = ?",
            ScheduleMapper.INSTANCE, assessmentId, examId);
    }

    public Optional<Schedule> create(Long assessmentId, Long examId, Interval<LocalDateTime> period) {
        final String query = "INSERT INTO schedule(assessment_id, exam_id, period) VALUES (?, ?, ?) RETURNING *";
        return parametrizedJdbcTemplate.getJdbcTemplate()
            .query(query, ScheduleMapper.INSTANCE, assessmentId, examId, SqlUtils.localDateTimeRange(period)).stream().findAny();
    }

    public List<Schedule> deleteAllByAssessmentIdAndExamId(Long assessmentId, Long examId) {
        return parametrizedJdbcTemplate.getJdbcTemplate()
            .query("DELETE FROM schedule WHERE assessment_id = ? AND exam_id = ? RETURNING *", ScheduleMapper.INSTANCE, assessmentId, examId);
    }

    public Optional<Schedule> deleteById(Long id) {
        return parametrizedJdbcTemplate.getJdbcTemplate()
            .query("DELETE FROM schedule WHERE id = ? RETURNING *", ScheduleMapper.INSTANCE, id).stream().findAny();
    }

    public List<Schedule> deleteAllByAssessmentId(Long assessmentId) {
        return parametrizedJdbcTemplate.getJdbcTemplate()
            .query("DELETE FROM schedule WHERE assessment_id = ? RETURNING *", ScheduleMapper.INSTANCE, assessmentId);
    }

    public void deleteExpired() {
        parametrizedJdbcTemplate.getJdbcTemplate().update("DELETE FROM schedule WHERE UPPER(period) < NOW()");
    }

    public void deleteAllExceptIds(List<Long> ids) {
        parametrizedJdbcTemplate.update("DELETE FROM schedule WHERE id NOT IN (:ids)", new MapSqlParameterSource("ids", ids));
    }

    private static class ScheduleMapper implements RowMapper<Schedule> {
        private static final ScheduleMapper INSTANCE = new ScheduleMapper();

        @Override
        public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Schedule(
                rs.getLong("id"),
                rs.getLong("assessment_id"),
                rs.getLong("exam_id"),
                SqlUtils.localDateTimeIntervalOf(rs.getObject("period"))
            );
        }
    }

}
