package com.mgnt.utils.textutils;

/**
 * @author Michael Gantman
 */
public class InvalidVersionRangeException extends Exception {

    private static final long serialVersionUID = -8185126644908932277L;

    public InvalidVersionRangeException() {
    }

    public InvalidVersionRangeException(String message) {
        super(message);
    }

    public InvalidVersionRangeException(Throwable cause) {
        super(cause);
    }

    public InvalidVersionRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidVersionRangeException(String message, Throwable cause,
                                        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
