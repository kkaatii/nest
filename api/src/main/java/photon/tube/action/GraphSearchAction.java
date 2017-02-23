package photon.tube.action;

import photon.tube.action.searcher.Searcher;
import photon.tube.cache.Cache;
import photon.tube.model.Owner;
import photon.tube.query.SortedGraphContainer;

/**
 * Created by Dun Liu on 2/20/2017.
 */
public class GraphSearchAction extends SearchAction {

    public GraphSearchAction(ActionManager manager,
                             Searcher searcher,
                             Cache<ActionRequest, SortedGraphContainer> cache,
                             Owner owner,
                             Object... params) {
        super(manager, searcher, cache, owner, params);
    }

    @Override
    public SortedGraphContainer doRun(Void _void) {
        return searcher.search(owner, params);
    }
}
