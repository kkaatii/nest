package photon.search;

import photon.crud.OafService;
import photon.crud.UnauthorizedException;
import photon.crud.CrudService;
import photon.model.Owner;
import photon.query.GraphContainer;
import photon.util.GenericDict;

abstract class Searcher {

    final CrudService crudService;
    final OafService oafService;

    public Searcher(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    public abstract GraphContainer search(Owner owner, GenericDict params)
            throws UnauthorizedException;

}
