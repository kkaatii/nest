package photon.service;

import photon.query.Query;
import photon.query.QueryResult;

public interface QueryService {
    QueryResult execute(Query query);
}
