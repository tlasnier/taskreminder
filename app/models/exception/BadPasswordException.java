package models.exception;

/**
 * Created by Thibault on 29/01/14.
 */
public class BadPasswordException extends Exception {
    public BadPasswordException() {
        super();
    }

    public BadPasswordException(String message) {
        super(message);
    }

    public BadPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
