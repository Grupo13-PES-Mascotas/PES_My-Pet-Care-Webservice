package org.pesmypetcare.webservice.error;

import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Creates the http response for the FirebaseAuthException class.
     * @param ex The exception from which to create the response
     * @return The response entity created from the exception
     */
    @ExceptionHandler(FirebaseAuthException.class)
    protected ResponseEntity<Object> handleAuthenticationException(
        FirebaseAuthException ex) {
        String errorMessage = new FirebaseExceptionHandler().getErrorMessage(ex.getErrorCode());
        ErrorBody errorBody = new ErrorBody(errorMessage, ex);
        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates the http response for the IllegalArgumentException class.
     * @param ex The exception from which to create the response
     * @return The response entity created from the exception
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorBody errorBody = new ErrorBody("Invalid argument", ex);
        return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates the http response for the DatabaseAccessException class.
     * @param ex The exception from which to create the response
     * @return The response entity created from the exception
     */
    @ExceptionHandler(DatabaseAccessException.class)
    protected ResponseEntity<Object> handleInvalidAccessToDatabase(DatabaseAccessException ex) {
        ErrorBody errorBody = new ErrorBody(ex.getErrorCode(), ex);
        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
