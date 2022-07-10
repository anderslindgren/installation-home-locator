package se.javatomten.homelocator;

/**
 * Relative Location has not been set.
 */
public class RelativeLocationNotSetException extends RuntimeException {
    public RelativeLocationNotSetException(String message) {
        super(message);
    }
}
