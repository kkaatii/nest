package nest.query;

/**
 * Created by Dun Liu on 5/28/2016.
 */
public class QueryResult {
    private String qid;
    private GraphInfo info;
    private GraphSlice slice;

    public QueryResult(String qid) {
        this.qid = qid;
    }

    public QueryResult withInfo(GraphInfo graphInfo) {
        this.info = graphInfo;
        return this;
    }

    public QueryResult withSlice(GraphSlice g) {
        this.slice = g;
        return this;
    }

    public String getQid() {
        return qid;
    }

    public GraphInfo getInfo() {
        return info;
    }

    public GraphSlice getSlice() {
        return slice;
    }
}
