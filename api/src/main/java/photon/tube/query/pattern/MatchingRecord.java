package photon.tube.query.pattern;

import java.util.ArrayList;
import java.util.List;

public class MatchingRecord<T> {
    public int id;
    public int depth;
    public PatternSegment<T> segment;
    public List<MatchingRecord<T>> parents;

    public MatchingRecord(int id, int depth, PatternSegment<T> segment) {
        this.id = id;
        this.depth = depth;
        this.segment = segment;
        this.parents = new ArrayList<>();
    }
}
