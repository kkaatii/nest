package photon.query.search;

import photon.action.ActionRequest;
import photon.action.Transformation;
import photon.model.Owner;
import photon.query.GraphContainer;

import static photon.Conventions.DICT_KEY_OWNER;

public class SearchAction extends Transformation<GraphContainer, GraphContainer> {

    private final Searcher searcher;
    private final ActionRequest request;

    SearchAction(Searcher searcher,
                        ActionRequest request) {
        this.searcher = searcher;
        this.request = request;
    }

    @Override
    protected GraphContainer transform(GraphContainer input) {
        return searcher.search(request.get(Owner.class, DICT_KEY_OWNER), request);
    }

}
