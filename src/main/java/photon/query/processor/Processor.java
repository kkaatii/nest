package photon.query.processor;

import photon.query.ArgumentClassMismatchException;
import photon.query.GraphContainer;

/**
 * Created by Dun Liu on 5/28/2016.
 */
public interface Processor {
    GraphContainer process(Object... args) throws ArgumentClassMismatchException;
}
