package photon.query;

/**
 * Created by Dun Liu on 5/28/2016.
 */
public class QueryResult {
    private Query query;
    private GraphInfo info;
    private GraphSlice slice;

    public QueryResult(Query query) {
        this.query = query;
    }

    public QueryResult withInfo(GraphInfo graphInfo) {
        this.info = graphInfo;
        return this;
    }

    public QueryResult withSlice(GraphSlice g) {
        this.slice = g;
        return this;
    }

    public Query getQuery() {
        return query;
    }

    public GraphInfo getInfo() {
        return info;
    }

    public GraphSlice getSlice() {
        return slice;
    }
}
