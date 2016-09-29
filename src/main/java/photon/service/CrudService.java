package photon.service;

import photon.data.*;

import java.util.List;
import java.util.Map;

/**
 * Provides CRUD service for all elements in a query: <tt>Chain</tt> (and its abbreviated version <tt>Point</tt>),
 * <tt>Arrow</tt> and <tt>Extension</tt>.
 */
public interface CrudService {

    // Save/update a node into db
    Node putNode(Node n);

    // Retrieve a node by its id of db
    Node getNode(int id);

    Point getPoint(int id);

    List<Point> getPoints(Iterable<Integer> nodeIdSet);

    Map<Integer, Point> getPointMap(Iterable<Integer> nodeIdSet);

    // Change the state of a node
    void updateNodeState(int id, NodeState ns);

    // Delete a node by its id
    void deleteNode(int id);

    // Draw an arrow between two nodes
    void putArrow(Arrow a);

    // Draw an arrow between two nodes fixateWith an extension
    void putArrow(Arrow a, Extension ext);

    List<Arrow> getAllArrowsBetween(Integer origin, Integer target);

    Arrow getArrow(Integer origin, ArrowType arrowType, Integer target);

    List<Arrow> getAllArrowsOriginatingFrom(Integer origin, ArrowType at);

    List<Extension> getExtensions(Iterable<Integer> extIdSet);

    // Add a new or modify an existing extension
    void updateExtension(Extension ext);

    // Erase the arrow between two nodes
    void deleteArrow(Arrow a);
}
