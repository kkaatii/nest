package photon.model;

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

    Node getNode(int id);

    String getNodeFrame(int id);

    Point getPoint(int id);

    List<Point> listPoints(Iterable<Integer> ids);

    Map<Integer, Point> pointMapOf(Collection<Integer> ids);

    void deleteNode(int id);

    void activateNode(int id, boolean active);

    void putArrow(Arrow a);

    List<FrameArrow> listFrameArrowsBetween(int origin, int target);

    Arrow getArrow(int origin, ArrowType at, int target);

    List<FrameArrow> listFrameArrowsStartingFrom(int origin, ArrowType at);

    void deleteArrow(Arrow a);

    List<Point> listPointsInFrame(String f);

    Map<Integer, Point> pointMapOwnedBy(int oid);

    List<Point> listPointsOwnedBy(int oid);

}
