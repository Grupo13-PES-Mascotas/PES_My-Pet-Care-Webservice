package org.pesmypetcare.webservice.error;

import java.util.HashMap;
import java.util.Map;

public class FirebaseExceptionHandler {
    private Map<String, String> errorMessage;

    public FirebaseExceptionHandler() {
        errorMessage = new HashMap<>();
        errorMessage.put("invalid-uid", "The username is invalid");
        errorMessage.put("invalid-email", "The email is invalid");
        errorMessage.put("invalid-password", "The password is invalid. It must be at leas 6 characters long");
        errorMessage.put("uid-already-exists", "The specified uid already exists");
        errorMessage.put("email-already-exists", "The specified email is already in use");
        errorMessage.put("user-not-found", "The user does not exist");
        errorMessage.put("internal-error", "An internal error occurred");
    }

    /**
     * Gets the error message associated with the specified error code.
     * @param errorCode The error code from which to get the message
     * @return The message associated with the specified error code
     */
    public String getErrorMessage(String errorCode) {
        String message;
        message = errorMessage.get(errorCode);
        if (message == null) {
            return "Unexpected error";
        }
        return message;
    }
}
