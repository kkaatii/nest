package photon.tube.query;

public interface QueryService {

    Query createQuery(QueryContext context);

    Query createQuery(String queryString);

}
