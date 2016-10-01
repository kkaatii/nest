package photon.api;

/**
 * Created by Dun Liu on 5/21/2016.
 */
public class WrongRequestFormatException extends RuntimeException {
    public WrongRequestFormatException(String message) {
        super(message);
    }
}
