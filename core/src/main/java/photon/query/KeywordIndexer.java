package photon.query;

import java.util.Collection;

/**
 * Created by Dun Liu on 4/28/2016.
 */
public interface KeywordIndexer {
    Collection<String> anatomize(String text);
}