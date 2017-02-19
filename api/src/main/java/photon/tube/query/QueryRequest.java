package photon.tube.query;

import photon.tube.model.Owner;

import java.util.Arrays;

/**
 * Stores contextual information for a specific query request.
 */
public class QueryRequest {
    public Owner owner;
    public String queryType;
    public Object[] args;
    public SegmentSpec segmentSpec;

    private QueryRequest(Owner owner, String queryType, Object[] args, SegmentSpec segmentSpec) {
        this.owner = owner;
        this.queryType = queryType;
        this.args = args;
        this.segmentSpec = segmentSpec;
    }

    // SegmentSpec is irrelevant to QueryRequest equivalency
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueryRequest request = (QueryRequest) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(args, request.args) && queryType.equals(request.queryType) && owner.equals(request.owner);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(args);
        result = 31 * result + queryType.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }

    public static class Builder {
        private String handler;
        private Object[] args;
        private SegmentSpec segmentSpec;
        private Owner owner;

        public Builder handler(String handler) {
            this.handler = handler;
            return this;
        }

        public Builder args(Object[] args) {
            this.args = args;
            return this;
        }

        public Builder segmentSpec(SegmentSpec segmentSpec) {
            this.segmentSpec = segmentSpec;
            return this;
        }

        public Builder owner(Owner owner) {
            this.owner = owner;
            return this;
        }

        public QueryRequest build() {
            return new QueryRequest(owner, handler, args, segmentSpec);
        }
    }
}
