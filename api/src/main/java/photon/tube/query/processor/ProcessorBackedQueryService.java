package photon.tube.query.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.cache.Cache;
import photon.tube.query.*;

/**
 * Dispatches a query to the designated <tt>Processor</tt> to process it and caches the <tt>SortedGraphContainer</tt>
 * result for repeated queries.
 */
@Service
public class ProcessorBackedQueryService implements QueryService {

    private final ProcessorProvider provider;
    private final Cache<QueryRequest, SortedGraphContainer> cache;
    private final QueryStringParser parser;

    @Autowired
    public ProcessorBackedQueryService(ProcessorProvider provider, Cache<QueryRequest, SortedGraphContainer> cache) {
        this.provider = provider;
        this.cache = cache;
        this.parser = new QueryStringParser();
    }

    private QueryResult processRequest(QueryRequest request) {
        try {
            SortedGraphContainer graphContainer = cache.get(request);
            if (graphContainer == null) {
                graphContainer = provider.getProcessor(request.queryType).process(request.owner, request.args);
                cache.put(request, graphContainer);
            }
            SortedGraphContainer segmentContainer = request.segmentSpec.applyTo(graphContainer);
            return new QueryResult(request, graphContainer.info(), segmentContainer.info(), segmentContainer.asSegment());
        } catch (UnauthorizedActionException uae) {
            throw uae;
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
    }

    @Override
    public Query createQuery(QueryRequest queryRequest) {
        return new Query(queryRequest) {
            private QueryResult result;

            @Override
            public QueryResult result() {
                if (result == null) result = processRequest(queryRequest);
                return result;
            }
        };
    }

    @Override
    public Query createQuery(String queryString) {
        QueryRequest request = parser.parse(queryString);
        return new Query(request) {
            @Override
            public QueryResult result() {
                return null;
            }
        };
    }
}
