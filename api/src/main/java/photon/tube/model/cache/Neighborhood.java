package photon.tube.model.cache;

import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;
import photon.tube.model.FrameArrow;

import java.util.*;
import java.util.stream.Collectors;

import static photon.util.Util.ensureList;

class Neighborhood {
	
	private Map<ArrowType, List<FrameArrow>> arrowMap = new HashMap<>();
	private int origin;
	
	Neighborhood(int origin) {
		this.origin = origin;
	}
	
	int getOriginId() {
		return origin;
	}
	
	void addArrow(FrameArrow arrow) {
		ArrowType type = arrow.getType();
		List<FrameArrow> list = arrowMap.get(type);
		if (list != null) list.add(arrow);
		else {
			list = new ArrayList<>();
			list.add(arrow);
			arrowMap.put(type, list);
		}
	}
	
	Set<Integer> neighborIdSet() {
		Set<Integer> set = new HashSet<>();
		for (List<FrameArrow> arrows : arrowMap.values()) {
			set.addAll(arrows.stream().map(Arrow::getTarget).collect(Collectors.toList()));
		}
		return set;
	}
	
	List<FrameArrow> arrowsByType(ArrowType type) {
		if (type == ArrowType.ANY) {
			List<FrameArrow> arrows = new ArrayList<>();
			arrowMap.values().forEach(arrows::addAll);
			return arrows;
		}
		return ensureList(arrowMap.get(type));
	}
	
	List<FrameArrow> arrowsByTarget(int target) {
		List<FrameArrow> arrows = new ArrayList<>();
		for (List<FrameArrow> l : arrowMap.values()) {
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
		for (List<FrameArrow> al : arrowMap.values()) {
			for (Iterator<FrameArrow> iter = al.iterator() ; iter.hasNext() ; )
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
		List<FrameArrow> list = arrowMap.get(a.getType());
		if (list != null) {
			Iterator<FrameArrow> iter = list.iterator();
			while (iter.hasNext()) {
				if (iter.next().similarTo(a)) iter.remove();
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(origin);
		str.append(": ");
		Collection<List<FrameArrow>> alc = arrowMap.values();
		for (List<FrameArrow> al : alc) {
			for (Arrow a : al) {
				str.append(a.toString());
				str.append(", ");
			}
		}
		return str.toString();
	}
}
