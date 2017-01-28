package photon.tube.query.processor;

import photon.tube.auth.OafService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.query.GraphContainer;

public abstract class Processor {

    final CrudService crudService;
    final OafService oafService;

    public Processor(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    public abstract GraphContainer process(Owner owner, Object... args)
            throws QueryArgumentClassException, UnauthorizedActionException;

}
