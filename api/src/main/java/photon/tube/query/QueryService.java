package photon.tube.query;

import photon.tube.action.Callback;

public interface QueryService {

    void executeQuery(QueryRequest request, Callback<QueryResult> listener);

}
