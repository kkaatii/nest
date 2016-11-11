package photon.tube.query.processor;


import photon.tube.query.QueryArgumentClassMismatchException;
import photon.tube.query.GraphContainer;

public interface Processor {
    GraphContainer process(Object... args) throws QueryArgumentClassMismatchException;
}
