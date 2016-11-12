package photon.tube.query.processor;


import photon.tube.model.Owner;
import photon.tube.query.QueryArgumentClassMismatchException;
import photon.tube.query.GraphContainer;

public interface Processor {
    GraphContainer process(Owner owner, Object... args) throws QueryArgumentClassMismatchException;
}
