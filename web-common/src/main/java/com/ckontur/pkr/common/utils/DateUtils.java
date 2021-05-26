package com.ckontur.pkr.common.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@UtilityClass
public class DateUtils {

    public static Date of(LocalDateTime localDateTime) {
        return of(localDateTime, ZoneId.systemDefault());
    }

    public static Date of(LocalDateTime localDateTime, ZoneId zone) {
        return Date.from(localDateTime.atZone(zone).toInstant());
    }

}
