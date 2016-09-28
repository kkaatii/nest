package nest.service;

import nest.query.Query;
import nest.query.QueryResult;

public interface QueryService {
    QueryResult execute(Query query);
}
