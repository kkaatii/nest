package photon.tube.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Provides CRUD service for all elements in a query: <tt>Chain</tt> (and its abbreviated version <tt>Point</tt>),
 * <tt>Arrow</tt> and <tt>Extension</tt>.
 */
public interface CrudService {

    Node putNode(Node n);

    Node updateNode(Node n);

    Node getNode(Integer id);

    String getNodeFrame(Integer id);

    Point getPoint(Integer id);

    List<Point> getPoints(Iterable<Integer> ids);

    Map<Integer, Point> getPointMap(Collection<Integer> ids);

    void deleteNode(Integer id);

    void activateNode(Integer id, boolean active);

    void putArrow(Arrow a);

    List<FrameArrow> getAllArrowsBetween(Integer origin, Integer target);

    Arrow getArrow(Integer origin, ArrowType at, Integer target);

    List<FrameArrow> getAllArrowsStartingFrom(Integer origin, ArrowType at);

    void deleteArrow(Arrow a);

    List<Point> getAllPointsInFrame(String f);

    Map<Integer, Point> getPointMapOwnedBy(Integer oid);

    List<Point> getPointsOwnedBy(Integer oid);

}
