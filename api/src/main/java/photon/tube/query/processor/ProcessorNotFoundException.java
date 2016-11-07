package photon.tube.query.processor;

/**
 * Created by Dun Liu on 5/29/2016.
 */
public class ProcessorNotFoundException extends RuntimeException {
    public ProcessorNotFoundException(Throwable cause) {
        super("Processor not found: ", cause);
    }

    public ProcessorNotFoundException(String s) {
        super(s);
    }
}
