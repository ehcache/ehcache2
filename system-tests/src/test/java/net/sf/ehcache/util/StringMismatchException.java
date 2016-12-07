package net.sf.ehcache.util;

/**
 * Created by CGRE on 04/07/2016.
 */
public class StringMismatchException extends Exception {
    public StringMismatchException() {
    }

    public StringMismatchException(String message) {
        super(message);
    }

    public StringMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public StringMismatchException(Throwable cause) {
        super(cause);
    }
}
