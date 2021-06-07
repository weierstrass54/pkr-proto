package com.ckontur.pkr.common.utils;

import lombok.experimental.UtilityClass;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class SqlUtils {
    private static final String INFINITY = "infinity";
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static <T> List<T> listOf(Array value, Function<String, T> mapper) throws SQLException {
        return streamOf(value, mapper).collect(Collectors.toList());
    }

    public static <T> Set<T> setOf(Array value, Function<String, T> mapper) throws SQLException {
        return streamOf(value, mapper).collect(Collectors.toSet());
    }

    public static <T> String array(Collection<T> values, Function<T, String> mapper) {
        return Optional.ofNullable(values)
            .map(v -> "{" + v.stream().map(mapper).collect(Collectors.joining(",")) + "}")
            .orElse(null);
    }

    public static Interval<LocalDateTime> localDateTimeIntervalOf(Object object) throws SQLException {
        String view = String.valueOf(object);
        List<LocalDateTime> values = Stream.of(view.substring(1, view.length() - 1).split(","))
            .map(v -> v.replaceAll("\"", ""))
            .map(v -> v.isEmpty() || v.equals(INFINITY) ? null : LocalDateTime.parse(v.substring(0, 19), DTF))
            .collect(Collectors.toList());
        if (values.size() != 2) {
            throw new SQLException("Интервал невалиден.");
        }
        return Interval.of(values.get(0), values.get(1), view.startsWith("["), view.endsWith("]"));
    }

    public static Object localDateTimeRange(Interval<LocalDateTime> localDateTimeInterval) {
        return (localDateTimeInterval.isIncludeStart() ? '[' : '(') +
            Optional.ofNullable(localDateTimeInterval.getStart()).map(d -> d.format(DTF)).orElse(INFINITY) +
            Optional.ofNullable(localDateTimeInterval.getFinish()).map(d -> d.format(DTF)).orElse(INFINITY) +
            (localDateTimeInterval.isIncludeFinish() ? ']' : ')');
    }

    private static <T> Stream<T> streamOf(Array value, Function<String, T> mapper) throws SQLException {
        return Stream.of((String[])value.getArray()).map(mapper);
    }

}
