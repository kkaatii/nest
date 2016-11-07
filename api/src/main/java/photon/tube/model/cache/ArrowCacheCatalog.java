package photon.tube.model.cache;

import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Acts as the cache of arrows.
 */
public class ArrowCacheCatalog {

    private Map<Integer, Neighborhood> records = new HashMap<>();

	@Override
	public String toString() {
		String linesep = System.getProperty("line.separator", "\n");
		StringBuilder value = new StringBuilder("GraphContainer" + linesep + "{" + linesep);
		for (Integer id : records.keySet()) {
			value.append("    ");
			Neighborhood ac = records.get(id);
			value.append(ac.toString());
			value.append(linesep);
		}
		value.append("}");
		return value.toString();
	}
	
	public String plainTextView(Integer... ids) {
		if (ids.length > 0) {
			StringBuilder value = new StringBuilder("");
			String linesep = System.getProperty("line.separator", "\n");
			for (int id : ids) {
				Neighborhood ac = neighborhoodOf(id);
				value.append(ac.toString());
				value.append(linesep);
			}
			return value.toString();
		}
		return toString();
	}
	
	public String htmlView(Integer... ids) {
		String linesep = "<br/>";
		StringBuilder value = new StringBuilder("Catalogue" + linesep + "{" + linesep);
		if (ids.length > 0) {
			for (int id : ids) {
				Neighborhood ac = neighborhoodOf(id);
				value.append(ac.toString());
				value.append(linesep);
			}
			return value.toString();
		}
		for (Integer id : records.keySet()) {
			value.append("    ");
			Neighborhood ac = records.get(id);
			value.append(ac.toString());
			value.append(linesep);
		}
		value.append("}");
		return value.toString();
	}

	public void recordArrow(Arrow arrow) {
		neighborhoodOf(arrow.getOrigin()).addArrow(arrow);
	}

    public void cache(Integer origin, List<Arrow> arrows) {
        if (arrows == null) {
            neighborhoodOf(origin);
        } else {
            recordArrow(arrows);
        }
    }

	public void recordArrow(List<Arrow> arrows) {
		for (Arrow a : arrows)
			recordArrow(a);
	}
	
	public void recordArrowsAndReverse(List<Arrow> arrows) {
        for (Arrow a : arrows) {
            recordArrow(a);
            recordArrow(a.reverse());
        }
	}
	
	public void eraseArrow(Arrow arrow) {
		Neighborhood neighborhood = records.get(arrow.getOrigin());
		if (neighborhood == null) return;
		neighborhood.remove(arrow);
	}
	
	private Neighborhood neighborhoodOf(Integer id) {
		Neighborhood neighborhood = records.get(id);
		if (neighborhood == null) {
			neighborhood = new Neighborhood(id);
			records.put(id, neighborhood);
		}
		return neighborhood;
	}

    public List<Arrow> arrowsByType(Integer origin, ArrowType arrowType) {
        return neighborhoodOf(origin).arrowsByType(arrowType);
    }

    public List<Arrow> arrowsBetween(Integer origin, Integer target) {
        return neighborhoodOf(origin).arrowsByTarget(target);
    }

    public Arrow arrow(Integer origin, ArrowType at, Integer target) {
        return neighborhoodOf(origin).arrowsByTargetAndType(target, at);
    }

    public Set<Integer> neighborIdSet(Integer origin) {
        return neighborhoodOf(origin).neighborIdSet();
    }
	
	public void removePoint(Integer id) {
		Neighborhood neighborhood = records.remove(id);
		if (neighborhood != null) {
			Set<Integer> related = neighborhood.neighborIdSet();
            related.stream().filter(i -> !i.equals(id)).forEach(i -> records.get(i).cleanNeighborById(id));
		}
	}
	
	public int arrowCount() {
		int n = 0;
		for (Neighborhood ac : records.values()) {
			if (ac != null) n += ac.size();
		}
		return (n / 2);
	}
	
	public Set<Integer> registries() {
		return records.keySet();
	}
	
	public boolean contains(Integer id) {
		return records.containsKey(id);
	}
	
}
		