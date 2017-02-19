package photon.tube.query.pattern;

import java.io.PushbackReader;

/**
 * Created by Dun Liu on 2/19/2017.
 */
public interface TokenParser<T> {
    T parse(PushbackReader reader);
}
