package photon.tube.query;

import photon.tube.model.Owner;

import java.util.Arrays;

/**
 * Stores contextual information for a specific query request.
 */
public class QueryContext {
    public Owner owner;
    public String type;
    public Object[] args;
    public SectionConfig sectionConfig;

    private QueryContext(Owner owner, String type, Object[] args, SectionConfig sectionConfig) {
        this.owner = owner;
        this.type = type;
        this.args = args;
        this.sectionConfig = sectionConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryContext context = (QueryContext) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(args, context.args) && type.equals(context.type) && owner.equals(context.owner);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(args);
        result = 31 * result + type.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }

    public static class Builder {
        private String type;
        private Object[] args;
        private SectionConfig sectionConfig;
        private Owner owner;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder args(Object[] args) {
            this.args = args;
            return this;
        }

        public Builder sectionConfig(SectionConfig sectionConfig) {
            this.sectionConfig = sectionConfig;
            return this;
        }

        public Builder owner(Owner owner) {
            this.owner = owner;
            return this;
        }

        public QueryContext build() {
            return new QueryContext(owner, type, args, sectionConfig);
        }
    }
}
