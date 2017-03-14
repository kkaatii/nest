package photon.tube.query;

import photon.tube.graph.GraphInfo;
import photon.tube.graph.SegmentGraph;

public class QueryResult {
    private GraphInfo graphInfo;
    private GraphInfo segmentInfo;
    private SegmentGraph segment;

    public QueryResult(Object actionOutcome) {

    }

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
