package photon.tube.cache;

import org.springframework.stereotype.Component;
import photon.tube.query.SortedGraphContainer;
import photon.tube.action.ActionRequest;

/**
 * Created by Dun Liu on 2/11/2017.
 */
@Component
public class GraphContainerCache implements Cache<ActionRequest, SortedGraphContainer> {
    @Override
    public SortedGraphContainer get(ActionRequest key) {
        return null;
    }

    @Override
    public boolean put(ActionRequest key, SortedGraphContainer value) {
        return false;
    }
}
