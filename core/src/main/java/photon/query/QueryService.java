package photon.query;

import photon.Callback;
import photon.model.Owner;

import java.util.concurrent.Future;

public interface QueryService {

    void executeQuery(Owner owner, String queryString, Callback<QueryResult> callback);

    Future<QueryResult> executeQuery(Owner owner, String queryString);

}
