package photon.tube.query;

import photon.tube.model.Owner;

import java.util.concurrent.Future;

public interface QueryService {

    void executeQuery(Owner owner, String queryString, QueryCallback<QueryResult> callback);

    Future<QueryResult> executeQuery(Owner owner, String queryString);

}
