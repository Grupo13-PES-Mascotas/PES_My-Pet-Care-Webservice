package org.pesmypetcare.webservice.error;

public class FirebaseExceptionHandler {
    protected FirebaseExceptionHandler() {
        throw new UnsupportedOperationException();
    }

    public static String getErrorMessage(String errorCode) {
        String message;
        switch (errorCode) {
            case "invalid-display-name":
                message = "The username is invalid. It must not be empty";
                break;
            case "invalid-email":
                message = "The email is invalid";
                break;
            case "invalid-password":
                message = "The password is invalid. It must be at leas 6 characters long";
                break;
            case "uid-already-exists":
                message = "The specified uid already exists";
                break;
            case "email-already-exists":
                message = "The specified email is already in use";
                break;
            case "user-not-found":
                message = "The user does not exist";
                break;
            case "internal-error":
                message = "An internal error occurred";
                break;
            default:
                message = "Unexpected error";
                break;
        }
        return message;
    }
}
