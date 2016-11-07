package photon.tube.query;

import photon.tube.model.*;

import java.util.Collection;

/**
 * The most simply representation of part of a graph to be sent back to the client side.
 */
public class Section {
    public final Collection<Point> points;
    public final Collection<Arrow> arrows;

    public Section(Collection<Point> points, Collection<Arrow> arrows) {
        this.points = points;
        this.arrows = arrows;
    }
}
