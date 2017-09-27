package photon.query.search;

import photon.auth.OafService;
import photon.auth.UnauthorizedException;
import photon.model.CrudService;
import photon.model.Owner;
import photon.query.GraphContainer;
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
