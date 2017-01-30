package photon;

import org.apache.commons.lang3.time.StopWatch;
import photon.tube.model.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockCrudService implements CrudService {
    private Map<Integer, Point> pointMap;
    private Map<Integer, List<FrameArrow>> neighbors;

    public MockCrudService() {
        pointMap = new HashMap<>();
        neighbors = new HashMap<>();
    }

    public void populateMockedElements(final int numNode, final int idealNumLayer, boolean containsCircle) {
        StopWatch watch = new StopWatch();
        watch.start();

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
        if (containsCircle) {
            // TODO add mocked circular arrows
        }

        watch.stop();
        System.out.println(String.format("Time cost for creating mock data: %f seconds", watch.getTime() / 1000f));
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
    public Node getNode(Integer id) {
        return null;
    }

    @Override
    public String getNodeFrame(Integer id) {
        return null;
    }

    @Override
    public Point getPoint(Integer id) {
        return null;
    }

    @Override
    public List<Point> getPoints(Iterable<Integer> ids) {
        return null;
    }

    @Override
    public Map<Integer, Point> getPointMap(Collection<Integer> ids) {
        return ids.stream().collect(Collectors.toMap(Function.identity(), pointMap::get));
    }

    @Override
    public void deleteNode(Integer id) {

    }

    @Override
    public void activateNode(Integer id, boolean active) {

    }

    // Avoid using this method to populate the mocked arrow map
    @Override
    public void putArrow(Arrow a) {
        List<FrameArrow> neighborhood = neighbors.computeIfAbsent(a.getOrigin(), k -> new ArrayList<>());
        neighborhood.add(new FrameArrow(a));
        neighborhood.add(new FrameArrow(a.reverse()));
    }

    @Override
    public List<FrameArrow> getAllArrowsBetween(Integer origin, Integer target) {
        List<FrameArrow> neighborhood = neighbors.get(origin);
        if (neighborhood == null)
            return new ArrayList<>();
        return neighborhood.stream().filter(fa -> fa.getTarget().equals(target)).collect(Collectors.toList());
    }

    @Override
    public Arrow getArrow(Integer origin, ArrowType at, Integer target) {
        return null;
    }

    @Override
    public List<FrameArrow> getAllArrowsStartingFrom(Integer origin, ArrowType at) {
        List<FrameArrow> neighborhood = neighbors.get(origin);
        if (neighborhood == null)
            return new ArrayList<>();
        return neighborhood.stream().filter(fa -> fa.getType().equals(at)).collect(Collectors.toList());
    }

    @Override
    public void deleteArrow(Arrow a) {

    }

    @Override
    public List<Point> getAllPointsInFrame(String f) {
        return null;
    }

    @Override
    public Map<Integer, Point> getPointMapOwnedBy(Integer oid) {
        return null;
    }

    @Override
    public List<Point> getPointsOwnedBy(Integer oid) {
        return null;
    }
}
