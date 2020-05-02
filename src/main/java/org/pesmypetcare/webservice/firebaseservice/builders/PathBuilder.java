package org.pesmypetcare.webservice.firebaseservice.builders;

/**
 * @author Santiago Del Rey
 */
public abstract class PathBuilder {
    /**
     * Throws a WrongNumberArgsException if the number of arguments does not match the expected.
     *
     * @param numExpected The number of argument expected
     * @param numArgs The number of arguments received
     */
    protected void throwExceptionIfWrongNumArgs(int numExpected, int numArgs) {
        if (numExpected != numArgs && (numExpected - 1) != numArgs) {
            throw new IllegalArgumentException(
                "Wrong number of arguments. Expected " + (numExpected - 1) + " or " + numExpected
                    + " arguments instead of " + numArgs);
        }
    }
}
