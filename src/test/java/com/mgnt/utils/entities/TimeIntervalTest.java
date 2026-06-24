package com.mgnt.utils.entities;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TimeIntervalTest {

    @Test
    void constructorAndConversions() {
        TimeInterval ti = new TimeInterval(2, TimeUnit.HOURS);
        assertEquals(2, ti.getValue());
        assertEquals(TimeUnit.HOURS, ti.getTimeUnit());
        assertEquals(7_200_000L, ti.toMillis());
        assertEquals(7_200L, ti.toSeconds());
        assertEquals(120L, ti.toMinutes());
        assertEquals(2L, ti.toHours());
        assertEquals(0L, ti.toDays());
    }

    @Test
    void nanosConversion() {
        TimeInterval ti = new TimeInterval(1, TimeUnit.MILLISECONDS);
        assertEquals(1_000_000L, ti.toNanos());
    }

    @Test
    void toDuration() {
        TimeInterval ti = new TimeInterval(90, TimeUnit.SECONDS);
        Duration d = ti.toDuration();
        assertEquals(90L, d.getSeconds());
    }

    @Test
    void toDurationThrowsWhenTimeUnitNull() {
        TimeInterval ti = new TimeInterval();
        assertThrows(IllegalArgumentException.class, ti::toDuration);
    }

    @Test
    void nullTimeUnitReturnsZeroForAllConversions() {
        TimeInterval ti = new TimeInterval();
        assertEquals(0L, ti.toMillis());
        assertEquals(0L, ti.toSeconds());
        assertEquals(0L, ti.toNanos());
        assertEquals(0L, ti.toMinutes());
        assertEquals(0L, ti.toHours());
        assertEquals(0L, ti.toDays());
    }

    @Test
    void compareToAndEqualsEquivalentIntervals() {
        TimeInterval sixtySeconds = new TimeInterval(60, TimeUnit.SECONDS);
        TimeInterval oneMinute    = new TimeInterval(1,  TimeUnit.MINUTES);
        assertEquals(0, sixtySeconds.compareTo(oneMinute));
        assertEquals(sixtySeconds, oneMinute);
        assertEquals(sixtySeconds.hashCode(), oneMinute.hashCode());
    }

    @Test
    void compareToOrdering() {
        TimeInterval a = new TimeInterval(1, TimeUnit.MINUTES);
        TimeInterval b = new TimeInterval(61, TimeUnit.SECONDS);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    void compareToNullReturnsPositive() {
        TimeInterval ti = new TimeInterval(1, TimeUnit.SECONDS);
        assertTrue(ti.compareTo(null) > 0);
    }

    @Test
    void notEqualToDifferentInterval() {
        TimeInterval a = new TimeInterval(1, TimeUnit.SECONDS);
        TimeInterval b = new TimeInterval(2, TimeUnit.SECONDS);
        assertNotEquals(a, b);
    }

    @Test
    void toStringFormat() {
        assertEquals("3 HOURS", new TimeInterval(3, TimeUnit.HOURS).toString());
        assertEquals("500 MILLISECONDS", new TimeInterval(500, TimeUnit.MILLISECONDS).toString());
    }

    @Test
    void setterGetter() {
        TimeInterval ti = new TimeInterval();
        ti.setValue(5);
        ti.setTimeUnit(TimeUnit.DAYS);
        assertEquals(5L, ti.getValue());
        assertEquals(TimeUnit.DAYS, ti.getTimeUnit());
        assertEquals(5L, ti.toDays());
    }
}
