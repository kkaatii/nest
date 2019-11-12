package photon.search;

import photon.crud.OafService;
import photon.crud.UnauthorizedException;
import photon.model.Arrow;
import photon.model.ArrowType;
import photon.crud.CrudService;
import photon.model.Owner;
import photon.query.GraphContainer;
import photon.util.GenericDict;

import java.util.ArrayList;
import java.util.List;

import static photon.crud.AccessLevel.READ;
import static photon.util.Utils.ensureList;

class WireSearcher extends Searcher {

    public WireSearcher(CrudService crudService, OafService oafService) {
        super(crudService, oafService);
    }

    @Override
    public GraphContainer search(Owner owner, GenericDict params)
            throws UnauthorizedException {
        int[] origins = params.get("origins", int[].class);
        int length = origins.length;
        if (length == 0) return GraphContainer.emptyContainer();

        ArrowType arrowType = ArrowType.extendedValueOf(params.get("arrow_type", String.class));
        List<Arrow> arrows = new ArrayList<>();
        Arrow tmp;
        for (int id : origins) {
            if (!oafService.authorized(READ, owner, crudService.getNodeFrame(id)))
                throw new UnauthorizedException();
        }
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (arrowType == ArrowType.WILDCARD)
                    arrows.addAll(crudService.listFrameArrowsBetween(origins[i], origins[j]));
                else if ((tmp = crudService.getArrow(origins[i], arrowType, origins[j])) != null)
                    arrows.add(tmp);
                else if ((tmp = crudService.getArrow(origins[i], arrowType.reverse(), origins[j])) != null)
                    arrows.add(tmp);
            }
        }
        return GraphContainer.fixateWith(ensureList(null), arrows);

    }
}