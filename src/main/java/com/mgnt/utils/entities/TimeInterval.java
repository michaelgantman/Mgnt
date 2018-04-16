package com.mgnt.utils.entities;

import java.util.concurrent.TimeUnit;

/**
 * This is convenience class that holds time interval as numerical value and its associated  {@link TimeUnit}
 * The class also provides methods of retrieval of its value as a long in needed scale (milliseconds, seconds, minutes,
 * hours or days) see methods {@link #toMillis()}, {@link #toSeconds()} , {@link #toMinutes()} , {@link #toHours()} ,
 * {@link #toDays()},
 */
public class TimeInterval {
    private long value;
    private TimeUnit timeUnit;

    /**
     * Time interval numerical value getter (without time unit)
     * @return Time Interval numerical value
     */
    public long getValue() {
        return value;
    }

    /**
     * Numerical value setter
     * @param value
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * {@link TimeUnit} getter
     * @return {@link TimeUnit} of this TimeInterval
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    /**
     * TimeUnit Setter
     * @param timeUnit
     */
    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * returns long value of this TimeInterval in milliseconds
     * @return long
     */
    public long toMillis() {
        long result = 0;
        if(timeUnit != null) {
            result = getTimeUnit().toMillis(getValue());
        }
        return result;
    }

    /**
     * returns long value of this TimeInterval in seconds
     * @return long
     */
    public long toSeconds() {
        long result = 0;
        if(timeUnit != null) {
            result = getTimeUnit().toSeconds(getValue());
        }
        return result;
    }

    /**
     * returns long value of this TimeInterval in minutes
     * @return long
     */
    public long toMinutes() {
        long result = 0;
        if(timeUnit != null) {
            result = getTimeUnit().toMinutes(getValue());
        }
        return result;
    }

    /**
     * returns long value of this TimeInterval in hours
     * @return long
     */
    public long toHours() {
        long result = 0;
        if(timeUnit != null) {
            result = getTimeUnit().toHours(getValue());
        }
        return result;
    }

    /**
     * returns long value of this TimeInterval in days
     * @return long
     */
    public long toDays() {
        long result = 0;
        if(timeUnit != null) {
            result = getTimeUnit().toDays(getValue());
        }
        return result;
    }
}