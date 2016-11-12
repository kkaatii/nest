package photon.tube.query;

import photon.tube.model.Owner;

import java.util.Arrays;

/**
 * Created by Dun Liu on 5/27/2016.
 */
public class Query {
    public Owner owner;
    public String type;
    public Object[] args;
    public SectionConfig sectionConfig;

    Query(Owner owner, String type, Object[] args, SectionConfig sectionConfig) {
        this.owner = owner;
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
        return Arrays.equals(args, query.args) && type.equals(query.type) && owner.equals(query.owner);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(args);
        result = 31 * result + type.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }
}
