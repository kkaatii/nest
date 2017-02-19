package photon.tube.query;

import photon.tube.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The simplest representation of part of a graph to be sent back to the client side.
 */
public class SegmentGraph {
    public final List<Point> points;
    public final Collection<Arrow> arrows;
    public final Map<Integer, List<Object>> depthToIds;

    SegmentGraph(List<Point> points, Collection<Arrow> arrows, Map<Integer, List<Integer>> depthToIndexes) {
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
        Set<Integer> depths = depthToIds.keySet().stream().sorted().collect(Collectors.toSet());
        StringBuilder sb = new StringBuilder();
        sb.append("---------------- Sorted Graph Segment ----------------\nNodes:\n");
        for (Integer depth:depths) {
            sb.append("  D").append(depth).append(":");
            List<Object> ids = depthToIds.get(depth);
            for (Object id : ids) {
                sb.append(" [").append(id).append("],");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
        }
        sb.append("Arrows:\n");
        for (Arrow a: arrows) {
            sb.append("  ").append(a.toString()).append("\n");
        }
        return sb.toString();
    }
}
