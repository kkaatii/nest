package photon.tube.service;

import photon.tube.model.*;

import java.util.List;
import java.util.Map;

/**
 * Provides CRUD service for all elements in a query: <tt>Chain</tt> (and its abbreviated version <tt>Point</tt>),
 * <tt>Arrow</tt> and <tt>Extension</tt>.
 */
public interface CrudService {

    // Save/update a node into db
    Node putNode(Node n);

    // Retrieve a node by its qid of db
    Node getNode(Integer id);

    Point getPoint(Integer id);

    List<Point> getPoints(Iterable<Integer> nodeIdSet);

    Map<Integer, Point> getPointMap(Iterable<Integer> nodeIdSet);

    // Delete a node by its qid
    void deleteNode(Integer id);

    void activateNode(Integer id, boolean active);

    // Draw an arrow between two nodes
    void putArrow(Arrow a);

    List<Arrow> getAllArrowsBetween(Integer origin, Integer target);

    Arrow getArrow(Integer origin, ArrowType arrowType, Integer target);

    List<Arrow> getAllArrowsOriginatingFrom(Integer origin, ArrowType at);

    // Erase the arrow between two nodes
    void deleteArrow(Arrow a);
}
