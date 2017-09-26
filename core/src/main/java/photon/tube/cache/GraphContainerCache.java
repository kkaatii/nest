package photon.tube.cache;

import org.springframework.stereotype.Component;
import photon.tube.query.GraphContainer;
import photon.tube.action.ActionRequest;

// TODO complete cache functions
@Component
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
