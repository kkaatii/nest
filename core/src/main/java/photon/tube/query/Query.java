package photon.tube.query;

import photon.tube.action.*;
import photon.tube.model.Owner;

import java.util.List;

/**
 */
public class Query {

    private final Owner owner;
    private QueryCallback<QueryResult> listener;
    private Transformation<?, QueryResult> lastAction;
    private List<ActionRequest> actionRequests;

    protected Query(Owner owner, QueryCallback<QueryResult> listener) {
        this.owner = owner;
        this.listener = listener;
    }

    public Owner owner() {
        return owner;
    }

    public void addAction(ActionRequest request) {
        actionRequests.add(request);
    }

    public void undoLastAction() {
    }

    public QueryResult result() {
        return null;
    }

    public void onException(Exception e) {

    }
}
