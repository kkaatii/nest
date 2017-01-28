package photon.tube.query;

import photon.tube.model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The most simple representation of part of a graph to be sent back to the client side.
 */
class SectionGraph {
    public final List<Point> points;
    public final List<Arrow> arrows;
    public final Map<Integer, List<Integer>> depthToIds;

    SectionGraph(List<Point> points, List<Arrow> arrows, Map<Integer, List<Integer>> depthToIndexes) {
        this.points = points;
        this.arrows = arrows;
        depthToIds = new HashMap<>();
        depthToIndexes.forEach((depth, indexes) -> depthToIds.put(
                depth,
                indexes.stream()
                        .map(index -> points.get(index).getId())
                        .collect(Collectors.toList())
        ));
    }
}
