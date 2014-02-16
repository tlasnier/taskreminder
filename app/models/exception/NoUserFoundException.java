package models.exception;

/**
 * Created by Thibault on 29/01/14.
 */
public class NoUserFoundException extends Exception {
    public NoUserFoundException() {
        super();
    }

    public NoUserFoundException(String message) {
        super(message);
    }

    public NoUserFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
