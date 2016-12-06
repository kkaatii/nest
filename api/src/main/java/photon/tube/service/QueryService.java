package photon.tube.service;

import photon.tube.query.Query;
import photon.tube.query.QueryContext;

public interface QueryService {

    Query createQuery(QueryContext context);
    Query createQuery(String string);

}
