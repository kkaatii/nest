package photon.tube.action.search;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedQueryException;
import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;
import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.graph.SortedGraphContainer;
import photon.util.GenericDict;

import java.util.ArrayList;
import java.util.List;

import static photon.tube.auth.AccessLevel.READ;
import static photon.util.Utils.ensureList;

public class WireSearcher extends Searcher {

    public WireSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public SortedGraphContainer search(Owner owner, GenericDict params)
            throws GraphSearchArgumentClassException, UnauthorizedQueryException {
        try {
            int[] origins = params.get(int[].class, "origins");
            int length = origins.length;
            if (length == 0) return SortedGraphContainer.emptyContainer();

            ArrowType arrowType = params.get(ArrowType.class, "arrow_type");
            List<Arrow> arrows = new ArrayList<>();
            Arrow tmp;
            for (int id : origins) {
                if (!oafService.authorized(READ, owner, crudService.getNodeFrame(id)))
                    throw new UnauthorizedQueryException();
            }
            for (int i = 0; i < length; i++) {
                for (int j = i + 1; j < length; j++) {
                    if (arrowType == ArrowType.ANY)
                        arrows.addAll(crudService.listArrowsBetween(origins[i], origins[j]));
                    else if ((tmp = crudService.getArrow(origins[i], arrowType, origins[j])) != null)
                        arrows.add(tmp);
                    else if ((tmp = crudService.getArrow(origins[i], arrowType.reverse(), origins[j])) != null)
                        arrows.add(tmp);
                }
            }
            return SortedGraphContainer.fixateWith(ensureList(null), arrows);

        } catch (ClassCastException e) {
            throw new GraphSearchArgumentClassException();
        }
    }
}
