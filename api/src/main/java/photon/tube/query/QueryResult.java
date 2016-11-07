package photon.tube.query;

public class QueryResult {
    private Query query;
    private GraphInfo info;
    private Section slice;

    public QueryResult(Query query) {
        this.query = query;
    }

    public QueryResult withInfo(GraphInfo graphInfo) {
        this.info = graphInfo;
        return this;
    }

    public QueryResult withSlice(Section g) {
        this.slice = g;
        return this;
    }

    public Query getQuery() {
        return query;
    }

    public GraphInfo getInfo() {
        return info;
    }

    public Section getSlice() {
        return slice;
    }
}
