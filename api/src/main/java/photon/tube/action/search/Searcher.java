package photon.tube.action.search;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedQueryException;
import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.graph.SortedGraphContainer;
import photon.util.GenericDict;

public abstract class Searcher {

    final CrudService crudService;
    final OafService oafService;

    public Searcher(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    public abstract SortedGraphContainer search(Owner owner, GenericDict params)
            throws GraphSearchArgumentClassException, UnauthorizedQueryException;

}
