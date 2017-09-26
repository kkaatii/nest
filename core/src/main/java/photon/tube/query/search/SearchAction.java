package photon.tube.query.search;

import photon.tube.action.ActionRequest;
import photon.tube.action.Transformation;
import photon.tube.model.Owner;
import photon.tube.query.GraphContainer;

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
