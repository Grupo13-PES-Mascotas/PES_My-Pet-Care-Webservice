package org.pesmypetcare.webservice.error;

/**
 * @author Marc Simó
 */
public class CalendarAccessException extends Exception {
    private final String errorCode;

    /**
     * Creates a calendar access exception with the specified error code and detailed message.
     * @param error The error code for the exception
     * @param message A clarifying description of the error
     */
    public CalendarAccessException(String error, String message) {
        super(message);
        this.errorCode = error;
    }

    /**
     * Gets the error code for the exception.
     * @return The error code for the exception
     */
    public String getErrorCode() {
        return errorCode;
    }
}
