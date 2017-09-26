package photon.tube.query.search;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedException;
import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.query.GraphContainer;
import photon.util.GenericDict;

public abstract class Searcher {

    final CrudService crudService;
    final OafService oafService;

    public Searcher(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    public abstract GraphContainer search(Owner owner, GenericDict params)
            throws UnauthorizedException;

}
