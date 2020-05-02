package org.pesmypetcare.webservice.firebaseservice.builders;

/**
 * @author Santiago Del Rey
 */
public abstract class PathBuilder {
    /**
     * Throws a WrongNumberArgsException if the number ofDocument arguments does not match the expected.
     *
     * @param numExpected The number ofDocument argument expected
     * @param numArgs The number ofDocument arguments received
     */
    protected void throwExceptionIfWrongNumArgs(int numExpected, int numArgs) {
        if (numExpected != numArgs && (numExpected - 1) != numArgs) {
            throw new IllegalArgumentException(
                "Wrong number ofDocument arguments. Expected " + (numExpected - 1) + " or " + numExpected + " arguments "
                    + "instead" + " ofDocument " + numArgs);
        }
    }
}
