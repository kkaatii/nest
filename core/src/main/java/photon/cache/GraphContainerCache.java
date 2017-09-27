package photon.cache;

import photon.action.ActionRequest;
import photon.query.GraphContainer;

// TODO complete cache functions
public class GraphContainerCache implements Cache<ActionRequest, GraphContainer> {
    @Override
    public GraphContainer get(ActionRequest key) {
        return null;
    }

    @Override
    public boolean put(ActionRequest key, GraphContainer value) {
        return false;
    }
}
