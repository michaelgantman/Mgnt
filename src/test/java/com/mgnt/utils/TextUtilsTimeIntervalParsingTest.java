package com.mgnt.utils;

import com.mgnt.utils.entities.TimeInterval;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilsTimeIntervalParsingTest {

    @Test
    void parseSeconds() {
        TimeInterval ti = TextUtils.parseStringToTimeInterval("38s");
        assertEquals(38, ti.getValue());
        assertEquals(TimeUnit.SECONDS, ti.getTimeUnit());
        assertEquals(38_000L, ti.toMillis());
    }

    @Test
    void parseMinutes() {
        TimeInterval ti = TextUtils.parseStringToTimeInterval("24m");
        assertEquals(TimeUnit.MINUTES, ti.getTimeUnit());
        assertEquals(1_440L, ti.toSeconds());
    }

    @Test
    void parseHours() {
        TimeInterval ti = TextUtils.parseStringToTimeInterval("4h");
        assertEquals(TimeUnit.HOURS, ti.getTimeUnit());
        assertEquals(4L, ti.toHours());
        assertEquals(14_400_000L, ti.toMillis());
    }

    @Test
    void parseDays() {
        TimeInterval ti = TextUtils.parseStringToTimeInterval("3d");
        assertEquals(TimeUnit.DAYS, ti.getTimeUnit());
        assertEquals(3L, ti.toDays());
    }

    @Test
    void parseNoSuffixDefaultsToMilliseconds() {
        TimeInterval ti = TextUtils.parseStringToTimeInterval("45");
        assertEquals(TimeUnit.MILLISECONDS, ti.getTimeUnit());
        assertEquals(45L, ti.toMillis());
    }

    @Test
    void parseUpperCaseSuffixCaseInsensitive() {
        TimeInterval ti = TextUtils.parseStringToTimeInterval("10S");
        assertEquals(TimeUnit.SECONDS, ti.getTimeUnit());
        assertEquals(10L, ti.getValue());
    }

    @Test
    void parseHoursUpperCase() {
        TimeInterval ti = TextUtils.parseStringToTimeInterval("2H");
        assertEquals(TimeUnit.HOURS, ti.getTimeUnit());
        assertEquals(2L, ti.toHours());
    }

    @Test
    void parseDaysUpperCase() {
        TimeInterval ti = TextUtils.parseStringToTimeInterval("5D");
        assertEquals(TimeUnit.DAYS, ti.getTimeUnit());
        assertEquals(5L, ti.toDays());
    }

    @Test
    void parseNullThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> TextUtils.parseStringToTimeInterval(null));
    }

    @Test
    void parseBlankThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> TextUtils.parseStringToTimeInterval("   "));
    }

    @Test
    void parseZeroThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> TextUtils.parseStringToTimeInterval("0s"));
    }

    @Test
    void parseNegativeThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> TextUtils.parseStringToTimeInterval("-5s"));
    }

    @Test
    void parseInvalidSuffixThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> TextUtils.parseStringToTimeInterval("5x"));
    }

    @Test
    void parseStringToDurationReturnsDuration() {
        Duration d = TextUtils.parseStringToDuration("5d");
        assertEquals(5L * 24 * 60 * 60, d.getSeconds());
    }

    @Test
    void parseStringToDurationSeconds() {
        Duration d = TextUtils.parseStringToDuration("30s");
        assertEquals(30L, d.getSeconds());
    }
}
