package photon.tube.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.action.search.SearcherFactory;

@Service
public class SearchService extends ActionFactory<SearchAction> {
    private static final String actionName = "search";
    private final SearcherFactory factory;

    @Autowired
    public SearchService(SearcherFactory factory) {
        super(actionName);
        this.factory = factory;
    }

 /*   private QueryResult processRequest(QueryRequest request) {
        try {
            SortedGraphContainer graphContainer = cache.get(request);
            if (graphContainer == null) {
                graphContainer = provider.findSearcher(request.queryType).search(request.owner, request.args);
                cache.put(request, graphContainer);
            }
            SortedGraphContainer segmentContainer = request.segmentSpec.applyTo(graphContainer);
            return new QueryResult(request, graphContainer.info(), segmentContainer.info(), segmentContainer.asSegment());
        } catch (UnauthorizedQueryException uae) {
            throw uae;
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
    }*/

    @Override
    public SearchAction createAction(ActionRequest request) {
        String searcherName = request.get(String.class, "searcher");
        return new SearchAction(
                factory.findSearcher(searcherName),
                request
        );
    }

}
