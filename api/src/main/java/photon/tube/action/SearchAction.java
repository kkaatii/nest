package photon.tube.action;

import photon.tube.action.search.Searcher;
import photon.tube.graph.SortedGraphContainer;

class SearchAction extends Transformation<SortedGraphContainer, SortedGraphContainer> {

    private final Searcher searcher;
    private final ActionRequest request;

    public SearchAction(Searcher searcher,
                        ActionRequest request) {
        this.searcher = searcher;
        this.request = request;
    }

    @Override
    protected SortedGraphContainer transform(SortedGraphContainer input) {
        return searcher.search(request.owner(), request);
    }

}
