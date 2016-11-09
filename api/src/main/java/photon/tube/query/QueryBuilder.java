package photon.tube.query;

public class QueryBuilder {
    private String type;
    private Object[] args;
    private SectionConfig sectionConfig;

    public QueryBuilder type(String type) {
        this.type = type;
        return this;
    }

    public QueryBuilder args(Object[] args) {
        this.args = args;
        return this;
    }

    public QueryBuilder sectionConfig(SectionConfig sectionConfig) {
        this.sectionConfig = sectionConfig;
        return this;
    }

    public Query build() {
        return new Query(type, args, sectionConfig);
    }
}