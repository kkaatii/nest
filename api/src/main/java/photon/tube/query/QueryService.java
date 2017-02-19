package photon.tube.query;

public interface QueryService {

    Query createQuery(QueryRequest queryRequest);

    Query createQuery(String queryString);

}
