package photon.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import photon.action.ActionRequest;
import photon.crud.MockOafService;
import photon.crud.MockCrudService;
import photon.crud.NodeActionFactory;
import photon.model.Owner;
import photon.search.SearchActionFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by dan on 23/09/2017.
 */
public class QueryTest {

    private final QueryServiceImpl queryService;
    private final String DEFAULT_JSON;

    public QueryTest() {
        MockCrudService mockCrudService = new MockCrudService();
        mockCrudService.initTestSet("simple");

        MockOafService mockOafService = new MockOafService();
        SearchActionFactory mockSearchActionFactory = new SearchActionFactory(mockCrudService, mockOafService);
        queryService = new QueryServiceImpl(mockSearchActionFactory,new NodeActionFactory(mockCrudService, mockOafService));

        StringBuilder actions = new StringBuilder();
        actions.append(",{\"action\":\"search\",\"arguments\":{\"searcher\":\"Mock\"}}");
        actions.append(",{\"action\":\"segment\",\"arguments\":{\"mode\":\"depth\",\"left_limit\":1,\"right_limit\":3,\"right_inclusive\":\"true\"}}");
        actions.deleteCharAt(0);
        DEFAULT_JSON = String.format("{\"actions\":[%s]}", actions);
    }

    public static void main(String[] args) throws IOException {
        QueryTest test = new QueryTest();

        if (args.length > 0) {
//            test.executeQueryTest(args[0]);
            test.parseJsonTest(args[0]);
        } else {
            test.executeQueryTest(null);
            test.parseJsonTest("{ \"actions\": [ { \"action\": \"node\", \"arguments\": {\"node\":{ \"id\": 123, \"name\": \"unknown\", \"content\": \"kawayi\", \"active\":true, \"frame\":null }} }] }");
        }
    }

    public void executeQueryTest(String s) {
        String json = s == null ? DEFAULT_JSON : s;
        queryService.executeQuery(new Owner(0, ""), json, input -> System.out.println(input.asJson()));
    }

    public void parseJsonTest(String s) throws IOException {
        String json = s == null ? DEFAULT_JSON : s;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode actions = objectMapper.readTree(json).get("actions");
        for (JsonNode action : actions) {
            ActionRequest request = new ActionRequest();
            queryService.parseActionJson(action, request);
            System.out.println(request.toString());
            System.out.println("--------------------------------");
        }
    }

    /**
     * Test <code>QueryService</code> with mock <code>CrudService</code> and mock <code>OafService</code>
     */
    void queryTest() {

        System.out.println("Start test:");

        queryService.executeQuery(
                new Owner(0, ""),
                chainSearchQuery(
                        new int[]{0},
                        "depth", 0, 1, true, true),
                input -> System.out.println(input.asJson())
        );

        queryService.executeQuery(
                new Owner(0, ""),
                sequencePatternSearchQuery(
                        new int[]{0},
                        new String[]{"dependent_on", "2*"},
                        "depth", 0, 1, true, true),
                input -> System.out.println(input.asJson())
        );
    }

    public static String chainSearchQuery(int[] origins, String segmentMode, int left, int right, boolean leftInclusive, boolean rightInclusive) {
        return String.format("{\"actions\":[" +
                "{\"action\":\"search\",\"arguments\":{\"searcher\":\"Chain\", \"origins\":%s}}" +
                "," +
                "{\"action\":\"segment\",\"arguments\":{\"mode\":\"%s\",\"left_limit\":%d,\"right_limit\":%d,\"left_inclusive\":\"%s\",\"right_inclusive\":\"%s\"}}" +
                "]}", Arrays.toString(origins), segmentMode, left, right, Boolean.toString(leftInclusive), Boolean.toString(rightInclusive));
    }

    public static String sequencePatternSearchQuery(int[] origins, String[] pattern, String segmentMode, int left, int right, boolean leftInclusive, boolean rightInclusive) {
        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = "\"" + pattern[i] + "\"";
        }
        return String.format("{\"actions\":[" +
                "{\"action\":\"search\",\"arguments\":{\"searcher\":\"SequencePattern\", \"origins\":%s, \"sequence\":%s}}" +
                "," +
                "{\"action\":\"segment\",\"arguments\":{\"mode\":\"%s\",\"left_limit\":%d,\"right_limit\":%d,\"left_inclusive\":\"%s\",\"right_inclusive\":\"%s\"}}" +
                "]}", Arrays.toString(origins), Arrays.toString(pattern), segmentMode, left, right, Boolean.toString(leftInclusive), Boolean.toString(rightInclusive));
    }

}
