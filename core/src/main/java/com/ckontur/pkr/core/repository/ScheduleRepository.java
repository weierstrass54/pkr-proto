package com.ckontur.pkr.core.repository;

import com.ckontur.pkr.common.utils.LocalDateTimeRange;
import com.ckontur.pkr.common.utils.SqlUtils;
import com.ckontur.pkr.core.model.Schedule;
import com.ckontur.pkr.core.web.ScheduleRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ScheduleRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Schedule> getAllByAssessmentId(Long assessmentId) {
        return jdbcTemplate.query(
            "SELECT * FROM schedule WHERE assessment_id = ? AND LOWER(period) > NOW()",
            ScheduleMapper.INSTANCE,
            assessmentId);
    }

    public List<Schedule> getAllByAssessmentId(Long assessmentId, Duration duration) {
        return jdbcTemplate.query(
            "SELECT * FROM schedule WHERE assessment_id = ? AND LOWER(period) BETWEEN NOW() AND ?",
            ScheduleMapper.INSTANCE,
            assessmentId, Timestamp.valueOf(LocalDateTime.now().plus(duration))
        );
    }

    @Transactional
    public List<Schedule> create(List<ScheduleRequests.CreateSchedule> schedule) {
        final String query = "INSERT INTO schedule(assessment_id, exam_id, period) VALUES (?, ?, ?) RETURNING *";
        return schedule.stream().flatMap(s ->
            s.getPeriods().stream()
                .map(SqlUtils::localDateTimeRange)
                .map(period -> jdbcTemplate.queryForObject(
                    query, ScheduleMapper.INSTANCE, s.getAssessmentId(), s.getExamId(), period)
                )
        ).collect(Collectors.toList());
    }

    public List<Schedule> deleteByAssessmentId(Long assessmentId) {
        return jdbcTemplate.query(
            "DELETE FROM schedule WHERE assessment_id = ? RETURNING *",
            ScheduleMapper.INSTANCE,
            assessmentId
        );
    }

    public void deleteAllNonExpired() {
        jdbcTemplate.query(
            "DELETE FROM schedule WHERE LOWER(period) > NOW() RETURNING *",
            ScheduleMapper.INSTANCE
        );
    }

    private static class ScheduleMapper implements RowMapper<Schedule> {
        private static final ScheduleMapper INSTANCE = new ScheduleMapper();

        @Override
        public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Schedule(
                rs.getLong("id"),
                null,
                SqlUtils.localDateTimeRangeOf(rs.getObject("period")),
                rs.getInt("available_count")
            );
        }
    }

}