package photon;

import photon.tube.action.*;
import photon.tube.auth.MockOafService;
import photon.tube.model.*;
import photon.tube.query.QueryService;
import photon.tube.query.QueryServiceImpl;
import photon.tube.query.search.SearchService;
import photon.tube.query.search.SearcherFactory;
import photon.util.PiEstimateAggregator;
import photon.util.Stopwatch;
import photon.util.Utils;

import java.util.*;

public class Test {

    public static void main(String... args) throws Exception {
        Test test = new Test();
        test.piTest(400, 100000);
//        test.parseNumberTest();
//        test.queryTest();
//        FrameArrow fa = new FrameArrow(1, ArrowType.PARENT_OF, 2);
//        System.out.println(fa.equals(fa.reverse().reverse()));
//        Set<Arrow> set = new HashSet<>();
//        set.add(fa);
//        System.out.println(set.contains(fa));
//        System.out.println(set.contains(fa.reverse().reverse()));
    }

    /**
     * Compare the speed of pi estimation of plain calculation vs. <code>Action</code> vs. native <code>Thread</code>
     */
    void piTest(int trials, int roundPerTrial) throws InterruptedException {
        Stopwatch stopwatchForNative = new Stopwatch();
        ActionTest test = new ActionTest();

        // Warm up
        stopwatchForNative.start();
        double sum = 0;
        for (int i = 0; i < 1; i++) {
            sum += test.calculatePi(roundPerTrial * trials);
        }
        stopwatchForNative.stop();
        System.out.println("Elapsed time (milliseconds): " + stopwatchForNative.millis());
        System.out.println("Estimated Pi is " + 4 * sum / 1);

        // Test on Action
        ActionManager manager = new ActionManager();
        Stopwatch stopwatchForAction = new Stopwatch() {
            @Override
            public void stop() {
                super.stop();
                manager.shutdown();
            }
        };
        PiEstimateAggregator aggregator = new PiEstimateAggregator(trials, stopwatchForAction);
        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < trials; i++) {
            ActionTest.NumSupplier supplier = new ActionTest.NumSupplier(roundPerTrial);
            actions.add(supplier
                    .then(Transformation.of(test::calculatePi))
                    .then(Transformation.of(aggregator::add).setAlwaysRerun(true))
            );
        }
        System.out.println("---------------  Begin test on Action  ---------------");
        stopwatchForAction.start();
        for (Action action : actions) {
            manager.schedule(action);
        }

//        // Warm up again
//        Thread.sleep(2000);
//        stopwatchForNative.start();
//        sum = 0;
//        for (int i = 0; i < 1; i++) {
//            sum += test.calculatePi(roundPerTrial*trials);
//        }
//        stopwatchForNative.stop();
//        System.out.println("Estimated Pi is " + 4 * sum / 1);
//        System.out.println("Elapsed time (milliseconds): " + stopwatchForNative.millis());
//
//        // Test on native Thread
//        Thread.sleep(5000);
//        PiEstimateAggregator counter2 = new PiEstimateAggregator(trials, stopwatchForNative);
//        List<Runnable> runnables = new ArrayList<>();
//        ExecutorService service = Executors.newFixedThreadPool(4);
//        for (int i = 0; i < trials; i++) {
//            ActionTest.NumSupplier supplier = new ActionTest.NumSupplier(roundPerTrial);
//            runnables.add(() -> counter2.add(test.calculatePi(supplier.output())));
//        }
//        System.out.println("---------------  Begin test on native thread  ---------------");
//        stopwatchForNative.start();
//        for (Runnable runnable : runnables) {
//            service.submit(runnable);
//        }
//        service.shutdown();

    }

    /**
     * Test Utils tools
     */
    void parseNumberTest() {
        System.out.println("1. should be parsable float: " + ((Utils.isParsable("1.") & Utils.FLAG_FLOAT) == Utils.FLAG_FLOAT ? "Success" : "Failure"));
        System.out.println("1.2 should be parsable float: " + (Utils.isParsable("1.2") == Utils.FLAG_FLOAT ? "Success" : "Failure"));
        System.out.println("-1 should be parsable int: " + (Utils.isParsable("-1") == Utils.FLAG_INT ? "Success" : "Failure"));
        System.out.println("+1. should be parsable float: " + (Utils.isParsable("+1.") == Utils.FLAG_FLOAT ? "Success" : "Failure"));
        System.out.println("+2.1. should NOT be parsable: " + (Utils.isParsable("+2.1.") == Utils.FLAG_NAN ? "Success" : "Failure"));
        System.out.println("1ss should NOT be parsable: " + (Utils.isParsable("1ss") == Utils.FLAG_NAN ? "Success" : "Failure"));
        System.out.println("+ should be NOT parsable: " + (Utils.isParsable("+") == Utils.FLAG_NAN ? "Success" : "Failure"));
        System.out.println(".5 should be NOT parsable: " + (Utils.isParsable(".5") == Utils.FLAG_NAN ? "Success" : "Failure"));
        System.out.println(Utils.isParsable(" 5"));
    }

    /**
     * Test <code>QueryService</code> with mock <code>CrudService</code> and mock <code>OafService</code>
     */
    void queryTest() {

        MockCrudService mockCrudService = new MockCrudService();
        mockCrudService.setAndInitTestSet("simple");

        SearchService mockSearchService = new SearchService(new SearcherFactory(mockCrudService, new MockOafService()));
        QueryService queryService = new QueryServiceImpl(mockSearchService);

        System.out.println("Start test:");

        queryService.executeQuery(
                new Owner(0, ""),
                chainSearchQuery(
                        new int[]{0},
                        "depth", 0, 1, true, true),
                input -> System.out.println(input.getSegment())
        );

        queryService.executeQuery(
                new Owner(0, ""),
                sequencePatternSearchQuery(
                        new int[]{0},
                        new String[]{"dependent_on", "2*"},
                        "depth", 0, 1, true, true),
                input -> System.out.println(input.getSegment())
        );
    }

    private String chainSearchQuery(int[] origins, String segmentMode, int left, int right, boolean leftInclusive, boolean rightInclusive) {
        return String.format("{\"actions\":[" +
                "{\"action\":\"search\",\"arguments\":{\"searcher\":\"Chain\", \"origins\":%s}}" +
                "," +
                "{\"action\":\"segment\",\"arguments\":{\"mode\":\"%s\",\"left_limit\":%d,\"right_limit\":%d,\"left_inclusive\":\"%s\",\"right_inclusive\":\"%s\"}}" +
                "]}", Arrays.toString(origins), segmentMode, left, right, Boolean.toString(leftInclusive), Boolean.toString(rightInclusive));
    }

    private String sequencePatternSearchQuery(int[] origins, String[] pattern, String segmentMode, int left, int right, boolean leftInclusive, boolean rightInclusive) {
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
