package photon.tube.query;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Dun Liu on 4/29/2016.
 */
@Component
public class DefaultKeywordIndexer implements KeywordIndexer {
    @Override
    public Collection<String> anatomize(String text) {
        return new ArrayList<>();
    }
}
