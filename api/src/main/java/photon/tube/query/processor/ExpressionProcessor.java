package photon.tube.query.processor;

import photon.tube.model.Owner;
import photon.tube.query.GraphContainer;
import photon.tube.service.AuthService;
import photon.tube.service.CrudService;

public class ExpressionProcessor extends Processor {

    public ExpressionProcessor(CrudService crudService, AuthService authService) {
        super(crudService, authService);
    }

    @Override
    public GraphContainer process(Owner owner, Object... args) throws QueryArgumentClassMismatchException {
        try {


            GraphContainer gc = new GraphContainer();


            return gc;
        } catch (ClassCastException cce) {
            throw new QueryArgumentClassMismatchException();
        }
    }
}
