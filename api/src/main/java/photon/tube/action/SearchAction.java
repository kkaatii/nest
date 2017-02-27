package photon.tube.action;

import photon.tube.action.searcher.Searcher;
import photon.tube.cache.Cache;
import photon.tube.model.Owner;
import photon.tube.query.SortedGraphContainer;

/**
 * Created by Dun Liu on 2/22/2017.
 */
abstract class SearchAction extends Action<Void, SortedGraphContainer> {

    protected final Owner owner;
    protected final Searcher searcher;
    protected final Cache<ActionRequest, SortedGraphContainer> cache;
    protected final Object[] params;

    protected SearchAction(ActionManager manager,
                           Searcher searcher,
                           Cache<ActionRequest, SortedGraphContainer> cache,
                           Owner owner,
                           Object... params) {
        super(manager);
        this.owner = owner;
        this.searcher = searcher;
        this.cache = cache;
        this.params = params;
    }

    @Override
    public void waitFor(Action<?, ? extends Void> predecessor) {
        throw new RuntimeException("Search action needs no predecessor!");
    }

}
