package photon.tube.query;

/**
 * A <tt>Query</tt> object manages the lifecycle of a query request. It contains the original
 * <tt>QueryContext</tt> (be it created from a REST request or by parsing a query string), and is able to return the
 * <tt>QueryResult</tt> in a controllable manner.
 * <p>
 * Each {@link QueryService} implementation shall be able to create a subclass of <tt>Query</tt>
 * which specifies how the <tt>result()</tt> method works.
 */
public abstract class Query {
    private QueryContext context;

    protected Query(QueryContext context) {
        this.context = context;
    }

    public QueryContext context() {
        return context;
    }

    public abstract QueryResult result();
}
