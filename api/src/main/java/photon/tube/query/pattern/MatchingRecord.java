package photon.tube.query.pattern;

import java.util.ArrayList;
import java.util.List;

public class MatchingRecord<T> {
    public int id;
    public int depth;
    public SequencePatternElement<T> patternElement;
    public List<MatchingRecord<T>> parents;

    public MatchingRecord(int id, int depth, SequencePatternElement<T> patternElement) {
        this.id = id;
        this.depth = depth;
        this.patternElement = patternElement;
        this.parents = new ArrayList<>();
    }
}
