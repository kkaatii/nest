package nest.query;

/**
 * Created by Dun Liu on 5/28/2016.
 */
public class FailedQueryException extends RuntimeException {
    public FailedQueryException(Throwable cause) {
        super("Failed query:", cause);
    }
}
