package org.pesmypetcare.webservice.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorBody {
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;

    public ErrorBody() {
        timestamp = LocalDateTime.now();
    }

    /**
     * Creator that sets the message to Unexpected error and gets the localized message from the exception.
     * @param ex The throwable from which to generate the body error
     */
    public ErrorBody(Throwable ex) {
        this();
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    /**
     * Creator with a specific message and exception.
     * @param message The message of the body
     * @param ex The throwable from which to generate the body error
     */
    public ErrorBody(String message, Throwable ex) {
        this();
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }

    /**
     * Creator with a specific timestamp, message and debug message.
     * @param timestamp The timestamp of the body
     * @param message The message of the body
     * @param debugMessage The debugMessage of the body
     */
    @JsonCreator
    public ErrorBody(@JsonProperty("timestamp") LocalDateTime timestamp,
                     @JsonProperty("message") String message,
                     @JsonProperty("debugMessage") String debugMessage) {
        this.timestamp = timestamp;
        this.message = message;
        this.debugMessage = debugMessage;
    }
}
