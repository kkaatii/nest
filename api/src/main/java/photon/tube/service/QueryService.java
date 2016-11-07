package photon.tube.service;

import photon.tube.query.Query;
import photon.tube.query.QueryResult;

public interface QueryService {

    QueryResult resultOf(Query q);

}
