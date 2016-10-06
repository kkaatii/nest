package photon.api;

/**
 * Created by Dun Liu on 5/21/2016.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
