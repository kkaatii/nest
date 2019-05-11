package photon.query;

import photon.model.Owner;

import java.util.concurrent.Future;

public interface QueryService {

    void executeQuery(Owner owner, String queryString, QueryCallback callback);

    Future<QueryResult> executeQuery(Owner owner, String queryString);

}
