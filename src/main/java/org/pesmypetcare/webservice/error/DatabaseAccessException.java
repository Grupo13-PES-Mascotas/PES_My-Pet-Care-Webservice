package org.pesmypetcare.webservice.error;

public class DatabaseAccessException extends Exception {
    //TODO: Document
    private final String errorCode;

    public DatabaseAccessException(String errorCode, String detailedMessage) {
        super(detailedMessage);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
