package photon.query.search;

import photon.action.ActionFactory;
import photon.action.ActionRequest;

import javax.inject.Inject;

public class SearchActionFactory extends ActionFactory<SearchAction> {
    private static final String actionName = "search";
    private final SearcherRegistry registry;

    @Inject
    public SearchActionFactory(SearcherRegistry registry) {
        super(actionName);
        this.registry = registry;
    }

    @Override
    public SearchAction createAction(ActionRequest request) {
        String searcherName = request.get(String.class, "searcher");
        return new SearchAction(registry.findSearcher(searcherName), request);
    }

}
