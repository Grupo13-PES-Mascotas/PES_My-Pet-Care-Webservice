package org.pesmypetcare.webservice.error;

public class DatabaseAccessException extends Exception {
    private final String errorCode;

    /**
     * Creates a database access exception with the specifies error code and detailed message.
     * @param errorCode The error code for the exception
     * @param detailedMessage A clarifying description of the error
     */
    public DatabaseAccessException(String errorCode, String detailedMessage) {
        super(detailedMessage);
        this.errorCode = errorCode;
    }

    /**
     * Gets the error code for the exception.
     * @return The error code for the exception
     */
    public String getErrorCode() {
        return errorCode;
    }
}
