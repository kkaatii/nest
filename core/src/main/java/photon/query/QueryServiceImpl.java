package photon.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import photon.action.*;
import photon.model.Arrow;
import photon.model.Node;
import photon.model.Owner;
import photon.crud.NodeActionFactory;
import photon.search.SearchActionFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

import static photon.query.Conventions.*;

public class QueryServiceImpl implements QueryService {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final Map<String, ActionFactory<?>> factories = new HashMap<>();

    @Inject
    public QueryServiceImpl(
            SearchActionFactory searchActionFactory,
            NodeActionFactory nodeActionFactory
    ) {
        factories.put(searchActionFactory.actionName(), searchActionFactory);
        factories.put(Segmentation.FACTORY.actionName(), Segmentation.FACTORY);
        factories.put(nodeActionFactory.actionName(), nodeActionFactory);
    }

    @Override
    public void executeQuery(Owner owner, String query, QueryCallback callback) {
        try {
            JsonNode actionsNode = jsonMapper.readTree(query).get(DICT_KEY_ACTIONS);
            Action headAction = new Action() {
                @Override
                protected void run() {
                    //TODO fetch cache?
                }
            };
            Action tailAction = headAction;
            for (JsonNode jsonNode : actionsNode) {
                ActionRequest request = new ActionRequest();
                request.put(DICT_KEY_OWNER, Owner.class, owner);
                parseActionJson(jsonNode, request);
                ActionFactory<?> factory = factories.get(request.actionName());
                if (factory == null)
                    throw new FailedQueryException("Undefined query action");
                tailAction = tailAction.then(factory.createAction(request));
            }
            tailAction
                    .then(new Transformation<Object, QueryResult>() {
                        @Override
                        protected QueryResult transform(Object input) {
                            try {
                                return new QueryResult(jsonMapper.writeValueAsString(input));
                            } catch (Exception e) {
                                throw new ActionRuntimeException(this, e);
                            }
                        }
                    })
                    .then(Transformation.of(callback::onSuccess));

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

    void parseActionJson(JsonNode json, ActionRequest request) throws IOException {
        String actionName = json.get(DICT_KEY_ACTION_NAME).asText();
        request.setActionName(actionName);
        JsonNode arguments = json.get(DICT_KEY_ARGUMENTS);
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
                    JsonNode elem = elements.next();
                    if (elem.isNumber()) {
                        List<Double> list = new ArrayList<>();
                        list.add(elem.asDouble());
                        boolean allInt = elem.isInt();
                        while (elements.hasNext()) {
                            elem = elements.next();
                            if (elem.isInt()) {
                                list.add(elem.asDouble());
                            } else if (elem.isFloat()) {
                                list.add(elem.asDouble());
                                allInt = false;
                            } else {
                                throw new IOException("Inconsistent type of array elements");
                            }
                        }
                        if (allInt) {
                            request.put(fieldName, int[].class, list.stream().mapToInt(Double::intValue).toArray());
                        } else {
                            request.put(fieldName, double[].class, list.stream().mapToDouble(d -> d).toArray());
                        }
                    } else {
                        List<String> list = new ArrayList<>();
                        list.add(elem.asText());
                        while (elements.hasNext()) {
                            list.add(elements.next().asText());
                        }
                        request.put(fieldName, String[].class, list.toArray(new String[0]));
                    }
                } else {
                    if (fieldNode.isInt()) {
                        request.put(fieldName, Integer.class, fieldNode.asInt());
                    } else if (fieldNode.isDouble()) {
                        request.put(fieldName, Double.class, fieldNode.asDouble());
                    } else if (fieldNode.isBoolean()) {
                        request.put(fieldName, Boolean.class, fieldNode.asBoolean());
                    } else if (fieldNode.isObject()) {
                        switch (fieldName) {
                            case DICT_KEY_NODE:
                                request.put(DICT_KEY_NODE, Node.class, jsonMapper.treeToValue(fieldNode, Node.class));
                                break;
                            case DICT_KEY_ARROW:
                                request.put(DICT_KEY_ARROW, Arrow.class, jsonMapper.treeToValue(fieldNode, Arrow.class));
                                break;
                            default:
                                request.put(fieldName, String.class, jsonMapper.writeValueAsString(fieldNode));
                        }
                    } else {
                        request.put(fieldName, String.class, fieldNode.asText());
                    }
                }
            }
        }
    }

}
