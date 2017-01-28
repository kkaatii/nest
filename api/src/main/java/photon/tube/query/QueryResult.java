package photon.tube.query;

public class QueryResult {
    private QueryContext context;
    private GraphInfo graphInfo;
    private GraphInfo sectionInfo;
    private SectionGraph section;

    public QueryResult(QueryContext context, GraphInfo graphInfo, GraphInfo sectionInfo, SectionGraph section) {
        this.context = context;
        this.graphInfo = graphInfo;
        this.sectionInfo = sectionInfo;
        this.section = section;
    }

    public QueryContext getContext() {
        return context;
    }

    public GraphInfo getGraphInfo() {
        return graphInfo;
    }

    public GraphInfo getSectionInfo() {
        return sectionInfo;
    }

    public SectionGraph getSection() {
        return section;
    }
}
