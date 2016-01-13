package com.mgnt.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
* This class is commented out because deep-common project does not have "Java EE" packages in its classpath. It only
* contains Java SE. As a result class javax.servlet.http.HttpServletRequest can not be resolved. This is the only
* problem with this class. If Java EE extention will be added to the classpath this class may be uncommented and
* used.
*/


/**
 * This class provides various web related utilities
 *
 * @author Michael Gantman
 */
public class WebUtils {
    //Default buffer size - 8K
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /*
     * This variable holds the maximum number of subsequent failures allowed. If we attempt to read from the source
     * and the number of read bytes returns as 0 yet no error was reported we will try to read again. but we don't
     * want to get stuck in the infinite loop if this situation keeps reoccuring. So we want to set upper limit
     * for the number of subsequent failures, after which we will give up and report an error.
     */
    public static final int MAX_SUBSEQUENT_FAILURES_LIMIT = 10;

    /**
     * This method reads the content of HTTPServletRequest as binary (raw) info without any modifications. I.e.
     * just as it was received.
     *
     * @param request HTTPServletRequest to be read
     * @return byte array that contains unmodified binary info read from request or null if no info was read
     * @throws IOException if any problems occurred.
     */
    public static byte[] readRequestContentAsByteArray(HttpServletRequest request) throws IOException {
        List<Byte> resultCollector = null;
        boolean unknownLength = true;
        try (InputStream reader = request.getInputStream()) {
            int length = request.getContentLength();
            switch (length) {
                case -1: {
                    length = DEFAULT_BUFFER_SIZE;
                    break;
                }
                case 0: {
                    /*
					 * Request has no content. It's not expected to occur, but just in case
					 */
                    return null;
                }
                default: {
                    unknownLength = false;
                }
            }
            if (unknownLength) {
                resultCollector = new ArrayList<Byte>(length);
            }
            int totalNumberOfReadBytes = 0;
            byte[] byteBuffer = new byte[length];
            int numberOfReadBytes = totalNumberOfReadBytes = reader.read(byteBuffer);
            if (numberOfReadBytes < 0) {
                return null;
            }
            if (!unknownLength && totalNumberOfReadBytes == length) {
                return byteBuffer;
            }
            if (unknownLength) {
                for (int i = 0; i < numberOfReadBytes; i++) {
                    resultCollector.add(byteBuffer[i]);
                }
            }
			/*
			 * If the original content length was unknown or if we did not read yet all the info then read until the end
			 */
            int numberOfsubsequentFailures = 0;
            do {
                if (!unknownLength) {
                    numberOfReadBytes = reader.read(byteBuffer, totalNumberOfReadBytes, length - totalNumberOfReadBytes);
                } else {
                    numberOfReadBytes = reader.read(byteBuffer);
                }
                if (numberOfReadBytes > 0) {
					/*
					 * This is the main or expected branch. It occurs if the reading from the source was successful
					 */
                    if (numberOfsubsequentFailures > 0) {
						/*
						 * We have read some bytes successfully. So if before this iteration there was some number
						 * of subsequent failures we just broke that sequence with current success. So we can reset
						 * the number of subsequent failures to 0
						 */
                        numberOfsubsequentFailures = 0;
                    }
                    if (unknownLength) {
                        for (int i = 0; i < numberOfReadBytes; i++) {
                            resultCollector.add(byteBuffer[i]);
                        }
                    }
                    totalNumberOfReadBytes += numberOfReadBytes;
                } else {
					/*
					 * This branch occurs If we attempt to read from the source and the number of read bytes returns as 0
					 * yet no error was reported we will try to read again. But we don't want to get stuck in the infinite
					 * loop if this situation keeps reoccuring. So we want to set upper limit for the number of subsequent
					 * failures uninterrupted by any successful read, after which we will give up and report an error.
					 */
                    if (++numberOfsubsequentFailures >= MAX_SUBSEQUENT_FAILURES_LIMIT) {
						/*
						 * We reached the maximum number of subsequent failures. So we give up and report a reading error
						 */
                        throw new IOException("The reading from the source could not be completed due to unknown error");
                    } else {
						/*
						 * If we just read 0 bytes don't immediately read again. Make a small pause in order not to
						 * exhaust processor resources and to let the source some time to give us something to read
						 */
                        TimeUtils.sleepFor(1L, TimeUnit.MILLISECONDS);
                    }
                }
            } while (numberOfReadBytes >= 0);
            if (!unknownLength && totalNumberOfReadBytes < length) {
                throw new IOException("The length of info read is less the declared content length");
            }
            if (unknownLength) {
                byteBuffer = new byte[resultCollector.size()];
                for (int i = 0; i < resultCollector.size(); i++) {
                    byteBuffer[i] = resultCollector.get(i);
                }
            }
            return byteBuffer;
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            throw new IOException("Unexpected error occurred", e);
        }
    }
}
