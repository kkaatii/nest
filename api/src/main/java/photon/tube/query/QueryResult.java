package photon.tube.query;

public class QueryResult {
    private Query query;
    private GraphInfo graphInfo;
    private GraphInfo sectionInfo;
    private Section section;

    public QueryResult(Query query) {
        this.query = query;
    }

    public QueryResult withGraphInfo(GraphInfo graphInfo) {
        this.graphInfo = graphInfo;
        return this;
    }

    public QueryResult withSectionInfo(GraphInfo sectionInfo) {
        this.sectionInfo = sectionInfo;
        return this;
    }

    public QueryResult withSection(Section section) {
        this.section = section;
        return this;
    }

    public Query getQuery() {
        return query;
    }

    public GraphInfo getGraphInfo() {
        return graphInfo;
    }

    public GraphInfo getSectionInfo() {
        return sectionInfo;
    }

    public Section getSection() {
        return section;
    }
}
