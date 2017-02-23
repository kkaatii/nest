package photon.tube.query;

import photon.tube.action.ActionListener;

public interface QueryService {

    void executeQuery(QueryRequest request, ActionListener<QueryResult> listener);

}
