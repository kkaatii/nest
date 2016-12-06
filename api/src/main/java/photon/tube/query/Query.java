package photon.tube.query;

/**
 * Created by Dun Liu on 5/27/2016.
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
