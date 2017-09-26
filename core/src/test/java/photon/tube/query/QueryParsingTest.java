package photon.tube.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import photon.tube.model.MockCrudService;
import photon.tube.auth.MockOafService;
import photon.tube.action.ActionException;
import photon.tube.action.ActionRequest;
import photon.tube.auth.OafService;
import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.query.search.SearchService;
import photon.tube.query.search.SearcherFactory;

import java.io.IOException;

/**
 * Created by dan on 23/09/2017.
 */
public class QueryParsingTest {

    private final QueryServiceImpl queryService;
    private final String DEFAULT_JSON;

    public QueryParsingTest() {
        CrudService crudService = new MockCrudService();
        OafService oafService = new MockOafService();
        SearchService searchService = new SearchService(new SearcherFactory(crudService, oafService));
        queryService = new QueryServiceImpl(searchService);

        StringBuilder actions = new StringBuilder();
        actions.append(",{\"action\":\"search\",\"arguments\":{\"searcher\":\"Mock\"}}");
        actions.append(",{\"action\":\"segment\",\"arguments\":{\"mode\":\"depth\",\"left_limit\":1,\"right_limit\":3,\"right_inclusive\":\"true\"}}");
        actions.deleteCharAt(0);
        DEFAULT_JSON = String.format("{\"actions\":[%s]}", actions);
    }

    public static void main(String[] args) throws IOException {
        QueryParsingTest test = new QueryParsingTest();

        if (args.length > 0) {
//            test.executeQueryTest(args[0]);
            test.parseJsonTest(args[0]);
        } else {
//            test.executeQueryTest(null);
            test.parseJsonTest(null);
        }
    }

    public void executeQueryTest(String s) {
        String json = s == null ? DEFAULT_JSON : s;
        queryService.executeQuery(new Owner(0, ""), json, input -> System.out.println(input.getSegment()));
    }

    public void parseJsonTest(String s) throws IOException {
        String json = s == null ? DEFAULT_JSON : s;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode actions = objectMapper.readTree(json).get("actions");
        for (JsonNode action : actions) {
            ActionRequest request = new ActionRequest();
            queryService.parseJson(action, request);
            System.out.println(request.toString());
            System.out.println("--------------------------------");
        }
    }
}
