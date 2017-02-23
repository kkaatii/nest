package photon.tube.query;

import photon.tube.action.*;
import photon.tube.model.Owner;

import java.util.List;

/**
 * A <tt>Query</tt> object manages the lifecycle of a query request. It contains the original
 * <tt>QueryRequest</tt> (be it created from a REST request or by parsing a query string), and is able to return the
 * <tt>QueryResult</tt> in a controllable manner.
 * <p>
 * Each {@link QueryService} implementation shall be able to create a subclass of <tt>Query</tt>
 * which specifies how the <tt>result()</tt> method works.
 */
public abstract class Query {

    private final Owner owner;
    private ActionListenerAction<QueryResult> listenerAction;
    private Action<?, QueryResult> lastAction;
    private List<ActionRequest> actionRequests;

    protected Query(Owner owner, ActionListenerAction<QueryResult> listenerAction) {
        this.owner = owner;
        this.listenerAction = listenerAction;
    }

    public Owner owner() {
        return owner;
    }

    public void addAction(ActionRequest request) {
        actionRequests.add(request);
    }

    public void undoLastAction() {
    }

    public void perform() {
        listenerAction.waitFor(lastAction);
        listenerAction.perform();
    }

    public boolean isDone() {
        return lastAction.isDone();
    }

    public QueryResult result() {
        if (lastAction != null && lastAction.isDone()) {
            return lastAction.result();
        } else {
            return null;
        }
    }

}
