package photon.tube.model.cache;

import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;

import java.util.*;
import java.util.stream.Collectors;

import static photon.util.Util.ensureList;

class Neighborhood {
	
	private Map<ArrowType, List<Arrow>> arrowMap = new HashMap<>();
	private int origin;
	
	Neighborhood(int origin) {
		this.origin = origin;
	}
	
	int getOriginId() {
		return origin;
	}
	
	void addArrow(Arrow arrow) {
		ArrowType type = arrow.getType();
		List<Arrow> list = arrowMap.get(type);
		if (list != null) list.add(arrow);
		else {
			list = new ArrayList<>();
			list.add(arrow);
			arrowMap.put(type, list);
		}
	}
	
	Set<Integer> neighborIdSet() {
		Set<Integer> set = new HashSet<>();
		for (List<Arrow> arrows : arrowMap.values()) {
			set.addAll(arrows.stream().map(Arrow::getTarget).collect(Collectors.toList()));
		}
		return set;
	}
	
	List<Arrow> arrowsByType(ArrowType type) {
		if (type == ArrowType.ANY) {
			List<Arrow> arrows = new ArrayList<>();
			arrowMap.values().forEach(arrows::addAll);
			return arrows;
		}
		return ensureList(arrowMap.get(type));
	}
	
	List<Arrow> arrowsByTarget(int target) {
		List<Arrow> arrows = new ArrayList<>();
		for (List<Arrow> l : arrowMap.values()) {
			arrows.addAll(l.stream().filter(a -> a.getTarget() == target).collect(Collectors.toList()));
		}
		return arrows;
	}

    Arrow arrowsByTargetAndType(int target, ArrowType type) {
        for (Arrow a : ensureList(arrowMap.get(type))) {
            if (a.getTarget() ==  target)
                return a;
        }
        return null;
    }
	
	void cleanNeighborById(int target) {
		for (List<Arrow> al : arrowMap.values()) {
			for (Iterator<Arrow> iter = al.iterator() ; iter.hasNext() ; )
				if (iter.next().getTarget() == target) iter.remove();
		}
	}
	
	int size() {
		int n = 0;
		for (List<?> list : arrowMap.values()) {
			n += list.size();
		}
		return n;
	}
	
	void remove(Arrow a) {
		List<Arrow> list = arrowMap.get(a.getType());
		if (list != null) {
			Iterator<Arrow> iter = list.iterator();
			while (iter.hasNext()) {
				if (iter.next().similarTo(a)) iter.remove();
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(origin);
		str.append(": ");
		Collection<List<Arrow>> alc = arrowMap.values();
		for (List<Arrow> al : alc) {
			for (Arrow a : al) {
				str.append(a.toString());
				str.append(", ");
			}
		}
		return str.toString();
	}
}
