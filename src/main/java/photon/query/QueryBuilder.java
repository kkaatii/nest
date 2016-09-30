package photon.query;

public class QueryBuilder {
    private String type;
    private Object[] args;
    private SliceConfig sliceConfig;

    public QueryBuilder type(String type) {
        this.type = type;
        return this;
    }

    public QueryBuilder args(Object[] args) {
        this.args = args;
        return this;
    }

    public QueryBuilder sliceConfig(SliceConfig sliceConfig) {
        this.sliceConfig = sliceConfig;
        return this;
    }

    public Query createQuery() {
        return new Query(type, args, sliceConfig);
    }
}