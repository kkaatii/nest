package photon.tube.query.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.action.ActionFactory;
import photon.tube.action.ActionRequest;

@Service
public class SearchService extends ActionFactory<SearchAction> {
    private static final String actionName = "search";
    private final SearcherFactory factory;

    @Autowired
    public SearchService(SearcherFactory factory) {
        super(actionName);
        this.factory = factory;
    }

    @Override
    public SearchAction createAction(ActionRequest request) {
        String searcherName = request.get(String.class, "searcher");
        return new SearchAction(factory.findSearcher(searcherName), request);
    }

}
