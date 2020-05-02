package org.pesmypetcare.webservice.error;

/**
 * @author Santiago Del Rey
 */
public class DocumentException extends Exception {
    private final String errorCode;

    /**
     * Creates a database access exception with the specified error code and detailed message.
     * @param errorCode The error code for the exception
     * @param detailedMessage A clarifying description ofDocument the error
     */
    public DocumentException(String errorCode, String detailedMessage) {
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
