package photon.query;

public class QueryResult {
    private GraphInfo graphInfo;
    private GraphInfo segmentInfo;
    private SegmentGraph segment;

    public QueryResult(GraphInfo graphInfo, GraphInfo segmentInfo, SegmentGraph segment) {
        this.graphInfo = graphInfo;
        this.segmentInfo = segmentInfo;
        this.segment = segment;
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
