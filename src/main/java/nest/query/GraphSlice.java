package nest.query;

import nest.data.Arrow;
import nest.data.Extension;
import nest.data.Point;

import java.util.*;

/**
 * The most simply representation of part of a graph to be sent back to the client side.
 */
public class GraphSlice {
    public final Collection<Point> points;
    public final Collection<Arrow> arrows;
    public final Collection<Extension> extensions;

    public GraphSlice(Collection<Point> points, Collection<Arrow> arrows, Collection<Extension> extensions) {
        this.points = points;
        this.arrows = arrows;
        this.extensions = extensions;
    }
}
