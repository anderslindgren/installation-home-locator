package se.javatomten.homelocator;

/**
 * If something goes wrong when locating home.
 */
public class HomeLocatorException extends RuntimeException {
    public HomeLocatorException(String message) {
        super(message);
    }

    public HomeLocatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
