package photon.tube.query;

import photon.tube.model.Owner;

public class QueryBuilder {
    private String type;
    private Object[] args;
    private SectionConfig sectionConfig;
    private Owner owner;

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

    public QueryBuilder owner(Owner owner) {
        this.owner = owner;
        return this;
    }

    public Query build() {
        return new Query(owner, type, args, sectionConfig);
    }
}