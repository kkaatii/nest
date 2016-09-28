package nest.service;

import nest.query.QueryContext;
import nest.query.QueryResult;

public interface QueryService {
    QueryResult query(QueryContext qc);
}
