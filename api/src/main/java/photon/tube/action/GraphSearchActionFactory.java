package photon.tube.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.cache.Cache;
import photon.tube.query.SortedGraphContainer;
import photon.tube.action.searcher.SearcherFactory;

@Service
public class GraphSearchActionFactory implements ActionFactory<SearchAction> {

    private final ActionManager actionManager = ActionManager.INSTANCE;
    private final SearcherFactory provider;
    private final Cache<ActionRequest, SortedGraphContainer> cache;
  //  private final QueryStringParser parser;

    @Autowired
    public GraphSearchActionFactory(SearcherFactory provider, Cache<ActionRequest, SortedGraphContainer> cache) {
        this.provider = provider;
        this.cache = cache;
//        this.parser = new QueryStringParser();
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
        } catch (UnauthorizedActionException uae) {
            throw uae;
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
    }*/

    @Override
    public SearchAction create(ActionRequest request) {
        return new GraphSearchAction(
                actionManager,
                provider.findSearcher(request.attributes.get("searcher")),
                cache,
                request.owner,
                request.params
        );
    }

}
