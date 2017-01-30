package photon.tube.query;

import photon.tube.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The simplest representation of part of a graph to be sent back to the client side.
 */
public class SectionGraph {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<Integer> depths = depthToIds.keySet().stream().sorted().collect(Collectors.toSet());
        for (Integer depth:depths) {
            sb.append("Level ").append(depth).append(":\n  ");
            List<Integer> ids = depthToIds.get(depth);
            for (Integer id : ids) {
                sb.append(id).append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
