package photon.tube.cache;

import org.springframework.stereotype.Component;
import photon.tube.query.SortedGraphContainer;
import photon.tube.query.QueryRequest;

/**
 * Created by Dun Liu on 2/11/2017.
 */
@Component
public class GraphContainerCache implements Cache<QueryRequest, SortedGraphContainer> {
    @Override
    public SortedGraphContainer get(QueryRequest key) {
        return null;
    }

    @Override
    public boolean put(QueryRequest key, SortedGraphContainer value) {
        return false;
    }
}
