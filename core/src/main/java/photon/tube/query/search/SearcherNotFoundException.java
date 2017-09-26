package photon.tube.query.search;

public class SearcherNotFoundException extends RuntimeException {
    public SearcherNotFoundException(Throwable cause) {
        super("Searcher not found: ", cause);
    }
}