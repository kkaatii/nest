package photon.tube.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.action.*;
import photon.tube.graph.SortedGraphContainer;
import photon.tube.model.Owner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class QueryServiceImpl implements QueryService {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final Map<String, ActionFactory<?>> factoryMap = new HashMap<>();

    @Autowired
    public QueryServiceImpl(SearchService searchService) {
        factoryMap.put(searchService.actionName(), searchService);
        factoryMap.put(Segmentation.FACTORY.actionName(), Segmentation.FACTORY);

    }

    @Override
    public void executeQuery(Owner owner, String queryString, Callback<QueryResult> callback) {
        try {
            JsonNode actionsNode = jsonMapper.readTree(queryString).get("actions");

            BeginAction<SortedGraphContainer> firstAction = new BeginAction<>(SortedGraphContainer::new);
            Action lastAction = firstAction;
            for (JsonNode jsonNode : actionsNode) {
                ActionRequest request = new ActionRequest(owner);
                parseJson(jsonNode.asText(), request);
                ActionFactory<?> factory = factoryMap.get(request.actionName());
                if (factory == null) {
                    throw new FailedQueryException("Failed query: undefined query type");
                }
                lastAction = lastAction.then(factory.createAction(request));
            }
            if (!(lastAction instanceof Segmentation)) {
                lastAction = lastAction.then(Segmentation.none());
            }
            lastAction.then(new CallbackAction<>(callback));

            firstAction.perform();
        } catch (IOException ioe) {
            throw new FailedQueryException("JSON parsing failed");
        }
    }

    @Override
    public Future<QueryResult> executeQuery(Owner owner, String queryString) {
        QueryFuture future = new QueryFuture();
        executeQuery(owner, queryString, future);
        return future;
    }

    public void parseJson(String json, ActionRequest request) throws IOException {
        // TODO WIP
    }


}
