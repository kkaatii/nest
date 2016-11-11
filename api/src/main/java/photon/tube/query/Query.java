package photon.tube.query;

import java.util.Arrays;

/**
 * Created by Dun Liu on 5/27/2016.
 */
public class Query {
    public Integer ownerId;
    public String type;
    public Object[] args;
    public SectionConfig sectionConfig;

    Query(Integer ownerId, String type, Object[] args, SectionConfig sectionConfig) {
        this.ownerId = ownerId;
        this.type = type;
        this.args = args;
        this.sectionConfig = sectionConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(args, query.args) && type.equals(query.type) && ownerId.equals(query.ownerId);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(args);
        result = 31 * result + type.hashCode();
        result = 31 * result + ownerId;
        return result;
    }
}
