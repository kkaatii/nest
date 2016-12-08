package photon.tube.query.processor;

import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.query.GraphContainer;
import photon.tube.auth.AuthService;

public abstract class Processor {

    final CrudService crudService;
    final AuthService authService;

    public Processor(CrudService crudService, AuthService authService) {
        this.crudService = crudService;
        this.authService = authService;
    }

    public abstract GraphContainer process(Owner owner, Object... args) throws QueryArgumentClassMismatchException;

}
