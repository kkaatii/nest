package photon.tube.query.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import photon.tube.auth.OafService;
import photon.tube.model.CrudService;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public final class SearcherFactory {
    private static final ConcurrentMap<String, Searcher> SEARCHER_MAP = new ConcurrentHashMap<>();
    private final CrudService crudService;
    private final OafService oafService;

    @Autowired
    public SearcherFactory(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    public Searcher findSearcher(String abbrSearcherName) {
        return SEARCHER_MAP.computeIfAbsent(abbrSearcherName, k -> {
            try {
                Class<?> clazz = Class.forName(completeName(abbrSearcherName));
                Constructor<?> ctor = clazz.getConstructor(CrudService.class, OafService.class);
                return (Searcher) ctor.newInstance(crudService, oafService);
            } catch (ClassNotFoundException ce) {
                throw new SearcherNotFoundException(ce);
            } catch (Exception e) {
                throw new SearcherInitFailedException(e);
            }
        });
    }

    private static String completeName(String name) {
        return String.format("photon.tube.query.search.%sSearcher", name);
    }
}
