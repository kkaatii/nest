package photon.tube.action.searcher;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.query.SortedGraphContainer;

public abstract class Searcher {

    final CrudService crudService;
    final OafService oafService;

    public Searcher(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    public abstract SortedGraphContainer search(Owner owner, Object... args)
            throws GraphSearchArgumentClassException, UnauthorizedActionException;

}
