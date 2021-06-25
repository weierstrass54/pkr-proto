package com.ckontur.pkr.common.utils;

import io.vavr.collection.*;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.experimental.UtilityClass;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class SqlUtils {
    private static final String INFINITY = "infinity";
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static <T> List<T> listOf(Array value, Function<String, T> mapper) throws SQLException {
        return List.ofAll(streamOf(value, mapper));
    }

    public static <T> Set<T> setOf(Array value, Function<String, T> mapper) throws SQLException {
        return HashSet.ofAll(streamOf(value, mapper));
    }

    public static <T> String array(Traversable<T> values, Function<T, String> mapper) {
        return Option.of(values)
            .map(v -> "{" + v.map(mapper).mkString(",") + "}")
            .getOrNull();
    }

    public static Interval<LocalDateTime> localDateTimeIntervalOf(Object object) throws SQLException {
        String view = String.valueOf(object);
        return Try.of(() ->
            Stream.of(view.substring(1, view.length() - 1).split(","))
                .map(v -> v.replaceAll("\"", ""))
                .map(v -> v.isEmpty() || v.equals(INFINITY) ? null : LocalDateTime.parse(v.substring(0, 19), DTF))
                .toList()
            )
            .filter(v -> v.size() == 2)
            .map(v -> Interval.of(v.get(0), v.get(1), view.startsWith("["), view.endsWith("]")))
            .getOrElseThrow(() -> new SQLException("Интервал невалиден."));
    }

    public static Object localDateTimeRange(Interval<LocalDateTime> localDateTimeInterval) {
        return (localDateTimeInterval.isIncludeStart() ? '[' : '(') +
            Option.of(localDateTimeInterval.getStart()).map(d -> d.format(DTF)).getOrElse(INFINITY) +
            Option.of(localDateTimeInterval.getFinish()).map(d -> d.format(DTF)).getOrElse(INFINITY) +
            (localDateTimeInterval.isIncludeFinish() ? ']' : ')');
    }

    private static <T> Stream<T> streamOf(Array value, Function<String, T> mapper) throws SQLException {
        return Stream.of((String[])value.getArray()).map(mapper);
    }

}
