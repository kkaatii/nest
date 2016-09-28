package nest.query;

/**
 * Created by Dun Liu on 5/28/2016.
 */
public interface Processor {
    GraphContainer process(Object... args) throws ArgumentClassMismatchException;
}
