package photon.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import photon.Callback;
import photon.action.*;
import photon.model.Owner;
import photon.query.search.SearchActionFactory;
import photon.util.Utils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

import static photon.Conventions.*;
import static photon.util.Utils.FLAG_FLOAT;
import static photon.util.Utils.FLAG_INT;

public class QueryServiceImpl implements QueryService {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final Map<String, ActionFactory<?>> factories = new HashMap<>();

    @Inject
    public QueryServiceImpl(SearchActionFactory searchActionFactory) {
        factories.put(searchActionFactory.actionName(), searchActionFactory);
        factories.put(Segmentation.FACTORY.actionName(), Segmentation.FACTORY);
    }

    @Override
    public void executeQuery(Owner owner, String query, Callback<QueryResult> callback) {
        try {
            JsonNode actionsNode = jsonMapper.readTree(query).get(JSON_KEY_ACTIONS);
            Action headAction = Transformation.of(GraphContainer::new);
            Action tailAction = headAction;
            for (JsonNode jsonNode : actionsNode) {
                ActionRequest request = new ActionRequest();
                request.put(Owner.class, DICT_KEY_OWNER, owner);
                parseJson(jsonNode, request);
                ActionFactory<?> factory = factories.get(request.actionName());
                if (factory == null) {
                    throw new FailedQueryException("Undefined query action");
                }
                tailAction = tailAction.then(factory.createAction(request));
            }

            if (!(tailAction instanceof Segmentation)) {
                tailAction = tailAction.then(Segmentation.none());
            }
            tailAction.then(Transformation.of(callback::onSuccess));
            ActionScheduler.getInstance().submit(headAction, callback);
        } catch (IOException ioe) {
            callback.onException(new FailedQueryException("JSON parsing failed because of ", ioe));
        } catch (Exception e) {
            callback.onException(e);
        }
    }

    @Override
    public Future<QueryResult> executeQuery(Owner owner, String queryString) {
        QueryFuture future = new QueryFuture();
        executeQuery(owner, queryString, future);
        return future;
    }

    static void parseJson(JsonNode json, ActionRequest request) throws IOException {
        String actionName = json.get(JSON_KEY_ACTION_NAME).asText();
        request.setActionName(actionName);
        JsonNode arguments = json.get(JSON_KEY_ARGUMENTS);
        if (arguments != null) {
            Iterator<String> fieldNames = arguments.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldNode = arguments.get(fieldName);
                if (fieldNode.isArray()) {
                    if (fieldNode.size() == 0) {
                        throw new IOException("Unknown array type of field \"" + fieldName + "\"");
                    }
                    Iterator<JsonNode> elements = fieldNode.elements();
                    String elem = elements.next().asText();
                    int elemFlag = Utils.isParsable(elem);
                    switch (elemFlag) {
                        case FLAG_INT:
                        case FLAG_FLOAT:
                            List<Double> list = new ArrayList<>();
                            list.add(Double.parseDouble(elem));
                            boolean allInt = elemFlag == FLAG_INT;
                            while (elements.hasNext()) {
                                elem = elements.next().asText();
                                elemFlag = Utils.isParsable(elem);
                                if (elemFlag == FLAG_INT) {
                                    list.add(Double.parseDouble(elem));
                                } else if (elemFlag == FLAG_FLOAT) {
                                    list.add(Double.parseDouble(elem));
                                    allInt = false;
                                } else {
                                    throw new IOException("Inconsistent type of array elements");
                                }
                            }
                            if (allInt) {
                                request.put(int[].class, fieldName, list.stream().mapToInt(Double::intValue).toArray());
                            } else {
                                request.put(double[].class, fieldName, list.stream().mapToDouble(d -> d).toArray());
                            }
                            break;
                        default:
                            List<String> list1 = new ArrayList<>();
                            list1.add(elem);
                            while (elements.hasNext()) {
                                elem = elements.next().asText();
                                list1.add(elem);
                            }
                            request.put(String[].class, fieldName, list1.toArray(new String[0]));
                    }
                } else {
                    String field = fieldNode.asText();
                    switch (Utils.isParsable(field)) {
                        case Utils.FLAG_BOOL:
                            request.put(Boolean.class, fieldName, Boolean.parseBoolean(field));
                            break;
                        case Utils.FLAG_INT:
                            request.put(Integer.class, fieldName, Integer.parseInt(field));
                            break;
                        case Utils.FLAG_FLOAT:
                            request.put(Double.class, fieldName, Double.parseDouble(field));
                            break;
                        default:
                            request.put(String.class, fieldName, field);
                    }
                }
            }
        }
    }

}
