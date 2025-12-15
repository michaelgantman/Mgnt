package com.mgnt.utils.entities;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * This is convenience class that holds time interval as numerical value and its associated  {@link TimeUnit}.
 * The class also provides methods of retrieval of its value as a long in needed scale (nanoseconds, milliseconds, 
 * seconds, minutes, hours or days) see methods {@link #toNanos()}, {@link #toMillis()}, {@link #toSeconds()}, 
 * {@link #toMinutes()}, {@link #toHours()}, {@link #toDays()}
 */
public class TimeInterval implements Comparable<TimeInterval> {
    private long value;
    private TimeUnit timeUnit;

    /**
     * Default constructor
     */
    public TimeInterval() {
    }

    /**
     * Constructor that allows to create initialized instance
     * @param interval time value
     * @param timeUnit TimeUnit that defines scale
     */
    public TimeInterval(long interval, TimeUnit timeUnit) {
        setValue(interval);
        setTimeUnit(timeUnit);
    }

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
     * returns long value of this TimeInterval in nanoseconds
     * @return long
     */
    public long toNanos() {
        long result = 0;
        if(timeUnit != null) {
            result = getTimeUnit().toNanos(getValue());
        }
        return result;
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
    
    /**
     * This methods converts this instance of {@link TimeInterval} class to instance of <b>java.time.Duration</b> class. This is a bridge method to convert 
     * this class to standard java JDK provided class from <b>java.time</b> package. The returned Duration class instance will have the same time interval with
     * the same resolution as this {@link TimeInterval} instance
     * @return java.time.Duration
     * @throws IllegalArgumentException if {@link #timeUnit} property of this instance is NULL
     */
    public Duration toDuration() throws IllegalArgumentException {
    	ChronoUnit chronoUnit = convertTimeUnitToChronoUnit();
    	if(chronoUnit == null) {
    		throw new IllegalArgumentException("TimeInterval could not be converted to Duration due to invalid TimeUnit value (most likely null)");
    	}
    	return Duration.of(getValue(), chronoUnit);
    }

    /**
     * Provides String representation of the value such as "3 MILLISECONDS".
     */
	@Override
	public String toString() {
		String result;
		if(timeUnit != null) {
			result = "" + getValue() + " " + timeUnit.toString();
		} else {
			result = super.toString();
		}
		return result;
	}

    @Override
    public int compareTo(TimeInterval o) {
        int result;
        if(o != null) {
            result = Long.compare(toMillis(), o.toMillis());
            if(result == 0) {
                result = Long.compare(toNanos(), o.toNanos());
            }
        } else {
            result = 1;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = true;
        if (this != o) {
            if (o == null || getClass() != o.getClass()) {
                result = false;
            } else {
                TimeInterval that = (TimeInterval) o;
                result = (this.compareTo(that) == 0);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(toMillis()).hashCode();
    }
    
    private ChronoUnit convertTimeUnitToChronoUnit() {
    	ChronoUnit result = null;
    	TimeUnit currentTimeUnit = getTimeUnit();
    	if(currentTimeUnit != null) {
    		switch(currentTimeUnit) {
    			case NANOSECONDS: {
    				result = ChronoUnit.NANOS;
    				break;
    			}
    			case MICROSECONDS: {
    				result = ChronoUnit.MICROS;
    				break;
    			}
    			case MILLISECONDS: {
    				result = ChronoUnit.MILLIS;
    				break;
    			}
    			case SECONDS: {
    				result = ChronoUnit.SECONDS;
    				break;
    			}
    			case MINUTES: {
    				result = ChronoUnit.MINUTES;
    				break;
    			}
    			case HOURS: {
    				result = ChronoUnit.HOURS;
    				break;
    			}
    			case DAYS: {
    				result = ChronoUnit.DAYS;
    				break;
    			}
    			default: {
    				result = null;
    				break;
    			}
    		}
    	}
    	return result;
    }
}
