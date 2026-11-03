package com.labequip.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * 日期工具类：LocalDate 解析/格式化、逾期天数计算
 */
public class DateUtil {

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** 今天 */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /** 解析 yyyy-MM-dd 字符串，失败返回 null */
    public static LocalDate parse(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(s.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** LocalDate -> yyyy-MM-dd，null 安全 */
    public static String fmt(LocalDate d) {
        return d == null ? "" : d.format(DATE_FMT);
    }

    /** Timestamp -> yyyy-MM-dd HH:mm，null 安全 */
    public static String fmt(Timestamp ts) {
        return ts == null ? "" : ts.toLocalDateTime().format(DATETIME_FMT);
    }

    /** b - a 的天数（可为负） */
    public static long daysBetween(LocalDate a, LocalDate b) {
        return ChronoUnit.DAYS.between(a, b);
    }
}
