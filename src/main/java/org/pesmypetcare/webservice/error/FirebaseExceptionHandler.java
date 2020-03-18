package org.pesmypetcare.webservice.error;

import java.util.HashMap;

public class FirebaseExceptionHandler {
    private HashMap<String, String> errorMessage;

    public FirebaseExceptionHandler() {
        errorMessage = new HashMap<>();
        errorMessage.put("invalid-display-name", "invalid-display-name");
        errorMessage.put("invalid-email", "The email is invalid");
        errorMessage.put("invalid-password", "The password is invalid. It must be at leas 6 characters long");
        errorMessage.put("uid-already-exists", "The specified uid already exists");
        errorMessage.put("email-already-exists", "The specified email is already in use");
        errorMessage.put("user-not-found", "The user does not exist");
        errorMessage.put("internal-error", "An internal error occurred");
    }

    public String getErrorMessage(String errorCode) {
        String message;
        message = errorMessage.get(errorCode);
        if (message == null)
            return "Unexpected error";
        return message;
    }
}
