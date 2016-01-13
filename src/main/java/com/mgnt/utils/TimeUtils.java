package com.mgnt.utils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * This is simple Utility class that has a convenience sleep method that "swallows"
 * Created by Michael Gantman on 22/12/2015.
 */
public class TimeUtils {

    /**
     * This method is a convenience method for sleep that "swallows" {@link InterruptedException}  and
     * has {@link TimeUnit} parameter in addition to time period so it makes it very convenient. So with
     * this method there is no need to convert the time into milliseconds. Just simply write
     * <br><br>
     * <p>{@code sleepFor(10, TimeUnit.SECONDS);}</p>
     * <br> No Exception catching needed
     * @param period time period to sleep
     * @param timeUnit {@link TimeUnit} that specifies in which units the time period is measured
     */
    public static void sleepFor(long period, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(period);
        } catch (InterruptedException ie) {
        }
    }
}
