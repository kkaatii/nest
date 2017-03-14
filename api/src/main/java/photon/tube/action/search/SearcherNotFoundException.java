package photon.tube.action.search;

/**
 * Created by Dun Liu on 5/29/2016.
 */
public class SearcherNotFoundException extends RuntimeException {
    public SearcherNotFoundException(Throwable cause) {
        super("Searcher not found: ", cause);
    }

    public SearcherNotFoundException(String s) {
        super(s);
    }
}
