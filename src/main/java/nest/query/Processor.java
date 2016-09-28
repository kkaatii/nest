package nest.query;

/**
 * Created by Dun Liu on 5/28/2016.
 */
public interface Processor {
    GraphContainer execute(Object... args) throws ArgumentClassMismatchException;
}
