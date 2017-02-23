package photon.tube.action.searcher;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;
import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.query.SortedGraphContainer;

import java.util.ArrayList;
import java.util.List;

import static photon.tube.auth.AccessLevel.READ;
import static photon.util.Utils.ensureList;

public class WireSearcher extends Searcher {

    public WireSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public SortedGraphContainer search(Owner owner, Object... args)
            throws GraphSearchArgumentClassException, UnauthorizedActionException {
        try {
            Integer[] ids = (Integer[]) args[0];
            int length = ids.length;
            if (length == 0) return SortedGraphContainer.emptyContainer();

            ArrowType arrowType = (ArrowType) args[1];
            List<Arrow> arrows = new ArrayList<>();
            Arrow tmp;
            for (Integer id : ids) {
                if (!oafService.authorized(READ, owner, crudService.getNodeFrame(id)))
                    throw new UnauthorizedActionException();
            }
            for (int i = 0; i < length; i++) {
                for (int j = i + 1; j < length; j++) {
                    if (arrowType == ArrowType.ANY)
                        arrows.addAll(crudService.getAllArrowsBetween(ids[i], ids[j]));
                    else if ((tmp = crudService.getArrow(ids[i], arrowType, ids[j])) != null)
                        arrows.add(tmp);
                    else if ((tmp = crudService.getArrow(ids[i], arrowType.reverse(), ids[j])) != null)
                        arrows.add(tmp);
                }
            }
            return SortedGraphContainer.fixateWith(ensureList(null), arrows);

        } catch (ClassCastException e) {
            throw new GraphSearchArgumentClassException();
        }
    }
}
