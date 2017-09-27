package photon.query;

/**
 * Created by Dun Liu on 5/28/2016.
 */
public class FailedQueryException extends RuntimeException {
    public FailedQueryException(String message, Throwable cause) {
        super(message + cause.getMessage(), cause);
    }

    public FailedQueryException(String message) {
        super(message);
    }
}
