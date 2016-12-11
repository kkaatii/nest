package photon.tube.query.processor;

/**
 * Created by Dun Liu on 5/28/2016.
 */
class QueryArgumentClassException extends RuntimeException {
    QueryArgumentClassException() {
        super("Query argument not expected");
    }
}
