package com.mgnt.utils;

import com.mgnt.utils.entities.TimeInterval;

import java.util.concurrent.TimeUnit;

/**
 * This is simple Utility class that has a convenience sleep method that "swallows"
 * Created by Michael Gantman on 22/12/2015.
 */
public class TimeUtils {

    /**
     * This method is a convenience method for method sleep() of classes Thread or TimeUnit. This method 
     * catches {@link InterruptedException} 
     * So when this method is used, there is no need to worry about catching {@link InterruptedException}
     * This method has {@link TimeUnit} parameter in addition to time period so it makes it very convenient. So with
     * this method there is no need to convert the time into milliseconds. Just simply write
     * <br><br>
     * <p>{@code sleepFor(10, TimeUnit.SECONDS);}</p>
     * <br> No Exception catching needed.<br>
     * <b>IMPORTANT NOTE</b>: When this method catches {@link InterruptedException} it interrupts current thread
     * so, that Thread Interruption mechanism continues to work when this method is used
     * @param period time period to sleep
     * @param timeUnit {@link TimeUnit} that specifies in which units the time period is measured
     */
    public static void sleepFor(long period, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(period);
        } catch (InterruptedException ie) {
        	Thread.currentThread().interrupt();
        }
    }

    /**
     * This is an overload for method {@link TimeUtils#sleepFor(long, TimeUnit)} which is provided for convenience.
     * Method {@link TimeUtils#sleepFor(long, TimeUnit)} could be very readable if you use actual values, for example
     <br><br>
     * <p>{@code sleepFor(10, TimeUnit.SECONDS);}</p>
     * <br>
     * But it you have your values stored in variables or especially already in instance of {@link TimeInterval}, then
     * it doesn't make it any more readable to write
     * <br><br>
     *     <p>{@code TimeUtils.sleepFor(timeInterval.getValue(), timeInterval.getTimeUnit());}</p>
     * <br><br>
     * Hence this convenience method is provided
     * @param timeInterval {@link TimeInterval} instance that holds the time period to be waited for
     */
    public static void sleepFor(TimeInterval timeInterval) {
        sleepFor(timeInterval.getValue(), timeInterval.getTimeUnit());
    }
}
