package photon.model;

import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockCrudService implements CrudService {
    private Map<Integer, Point> pointMap;
    private Map<Integer, List<FrameArrow>> neighbors;
    private final Map<String, Consumer<Object[]>> initMethods;

    public MockCrudService() {
        pointMap = new HashMap<>();
        neighbors = new HashMap<>();
        initMethods = new HashMap<>();
        initMethods.put("multilayer", this::populateMultilayer);
        initMethods.put("simple", this::populateSimple);
    }

    public void setAndInitTestSet(String setName, Object... args) {
        Consumer<Object[]> initMethod = initMethods.get(setName);

        if (initMethod == null) {
            throw new NullPointerException("Failed to find the test set: \"" + setName + "\"");
        }

        pointMap.clear();
        neighbors.clear();

        StopWatch watch = new StopWatch();

        watch.start();
        initMethod.accept(args);
        watch.stop();

        System.out.println(String.format("Test set initialized to %s in %d milliseconds", setName, watch.getTime()));
    }

    private void populateMultilayer(Object... args) {

        int numNode = (int) args[0];
        int idealNumLayer = (int) args[1];

        int numLayer = idealNumLayer > 0 ? idealNumLayer : (int) Math.floor(Math.sqrt(numNode));
        int numNodePerLayer = Math.floorDiv(numNode, numLayer);
        int residual = numNode - (numLayer - 1) * numNodePerLayer;
        int[] nodeIndex = new int[numLayer + 1];
        for (int i = 1; i < numLayer; i++) {
            nodeIndex[i] = numNodePerLayer;
        }
        nodeIndex[0] = 1;
        nodeIndex[numLayer] = residual;
        for (int i = 0; i <= numLayer; i++) {
            for (int j = 0; j < nodeIndex[i]; j++) {
                int index = i * numNodePerLayer + j;
                pointMap.put(index, new Point(index));
                neighbors.put(index, new ArrayList<>());
                if (i > 0) {
                    int pIndex;
                    for (int k = 0; k < nodeIndex[i - 1]; k++) {
                        pIndex = (i - 1) * numNodePerLayer + k;
                        FrameArrow fa = new FrameArrow(pIndex, ArrowType.values()[0], index);
                        neighbors.get(pIndex).add(fa);
                        // System.out.println(String.format("Arrow \"%s\" added", fa));
                    }
                }
            }
        }

    }

    /**
     * Relationship of nodes in this mock graph: <br>
     * <code>
     * [0]-- parent of -->[1]<br>
     *  &nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
     *  &nbsp;| dependent on&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| parent of<br>
     *  &nbsp;&darr;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
     * [2]<---------------&#8629;<br>
     *     &nbsp;|<br>
     *     &nbsp;| parent of + dependent on<br>
     *     &nbsp;&darr;<br>
     *     [3]
     * </code>
     * @param args not used
     */
    private void populateSimple(Object... args) {
        pointMap.put(0, new Point(0));
        pointMap.put(1, new Point(1));
        pointMap.put(2, new Point(2));
        pointMap.put(3, new Point(3));
        neighbors.put(0, Arrays.asList(new FrameArrow(0, ArrowType.PARENT_OF, 1), new FrameArrow(0, ArrowType.DEPENDENT_ON, 2)));
        neighbors.put(1, Arrays.asList(new FrameArrow(1, ArrowType.PARENT_OF, 2), new FrameArrow(1, ArrowType.PARENT_OF.reverse(), 0)));
        neighbors.put(2, Arrays.asList(
                new FrameArrow(2, ArrowType.PARENT_OF.reverse(), 1),
                new FrameArrow(2, ArrowType.DEPENDENT_ON.reverse(), 0),
                new FrameArrow(2, ArrowType.DEPENDENT_ON, 3),
                new FrameArrow(2, ArrowType.PARENT_OF, 3)
        ));
        neighbors.put(3, Arrays.asList(
                new FrameArrow(3, ArrowType.DEPENDENT_ON.reverse(), 2),
                new FrameArrow(3, ArrowType.PARENT_OF.reverse(), 2)
        ));
    }

    private Map<Integer, Point> pointMap() {
        return pointMap;
    }

    private Map<Integer, List<FrameArrow>> neighbors() {
        return neighbors;
    }

    @Override
    public Node putNode(Node n) {
        return null;
    }

    @Override
    public Node updateNode(Node n) {
        return null;
    }

    @Override
    public Node getNode(int id) {
        return null;
    }

    @Override
    public String getNodeFrame(int id) {
        return "";
    }

    @Override
    public Point getPoint(int id) {
        return null;
    }

    @Override
    public List<Point> listPoints(Iterable<Integer> ids) {
        return null;
    }

    @Override
    public Map<Integer, Point> pointMapOf(Collection<Integer> ids) {
        return ids.stream().collect(Collectors.toMap(Function.identity(), pointMap()::get));
    }

    @Override
    public void deleteNode(int id) {

    }

    @Override
    public void activateNode(int id, boolean active) {

    }

    // Avoid using this method to populate the mocked arrow map
    @Override
    public void putArrow(Arrow a) {
        List<FrameArrow> neighborhood = neighbors().computeIfAbsent(a.getOrigin(), k -> new ArrayList<>());
        neighborhood.add(new FrameArrow(a));
        neighborhood.add(new FrameArrow(a.reverse()));
    }

    @Override
    public List<FrameArrow> listFrameArrowsBetween(int origin, int target) {
        List<FrameArrow> neighborhood = neighbors().get(origin);
        if (neighborhood == null)
            return new ArrayList<>();
        return neighborhood.stream().filter(fa -> fa.getTarget().equals(target)).collect(Collectors.toList());
    }

    @Override
    public Arrow getArrow(int origin, ArrowType at, int target) {
        return null;
    }

    @Override
    public List<FrameArrow> listFrameArrowsStartingFrom(int origin, ArrowType at) {
        List<FrameArrow> neighborhood = neighbors().get(origin);
        if (neighborhood == null)
            return new ArrayList<>();
        return neighborhood.stream().filter(fa -> fa.getType().isType(at)).collect(Collectors.toList());
    }

    @Override
    public void deleteArrow(Arrow a) {

    }

    @Override
    public List<Point> listPointsInFrame(String f) {
        return null;
    }

    @Override
    public Map<Integer, Point> pointMapOwnedBy(int oid) {
        return null;
    }

    @Override
    public List<Point> listPointsOwnedBy(int oid) {
        return null;
    }
}
