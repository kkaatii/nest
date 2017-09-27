package photon.query.search;

import photon.action.ActionRequest;
import photon.action.Transformation;
import photon.model.Owner;
import photon.query.GraphContainer;

public class SearchAction extends Transformation<GraphContainer, GraphContainer> {

    public static final String KEY_FOR_OWNER = "owner";
    private final Searcher searcher;
    private final ActionRequest request;

    public SearchAction(Searcher searcher,
                        ActionRequest request) {
        this.searcher = searcher;
        this.request = request;
    }

    @Override
    protected GraphContainer transform(GraphContainer input) {
        return searcher.search(request.get(Owner.class, KEY_FOR_OWNER), request);
    }

}
