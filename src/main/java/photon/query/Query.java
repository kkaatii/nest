package photon.query;

import java.util.Arrays;

/**
 * Created by Dun Liu on 5/27/2016.
 */
public class Query {
    public String type;
    public Object[] args;
    public SliceConfig sliceConfig;

    public Query(String type, Object[] args, SliceConfig sliceConfig) {
        this.type = type;
        this.args = args;
        this.sliceConfig = sliceConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query query = (Query) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(args, query.args) && type.equals(query.type);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(args);
        result = 31 * result + type.hashCode();
        return result;
    }
}
