package photon.tube.query;

public class QueryResult {
    private QueryRequest request;
    private GraphInfo graphInfo;
    private GraphInfo segmentInfo;
    private SegmentGraph segment;

    public QueryResult(Object actionOutcome) {

    }

    public QueryResult(QueryRequest request, GraphInfo graphInfo, GraphInfo segmentInfo, SegmentGraph segment) {
        this.request = request;
        this.graphInfo = graphInfo;
        this.segmentInfo = segmentInfo;
        this.segment = segment;
    }

    public QueryRequest getRequest() {
        return request;
    }

    public GraphInfo getGraphInfo() {
        return graphInfo;
    }

    public GraphInfo getSegmentInfo() {
        return segmentInfo;
    }

    public SegmentGraph getSegment() {
        return segment;
    }
}
