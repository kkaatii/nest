package photon.query.search;

import photon.auth.OafService;
import photon.model.CrudService;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SearcherRegistry {
    private static final ConcurrentMap<String, Searcher> SEARCHER_MAP = new ConcurrentHashMap<>();
    private final CrudService crudService;
    private final OafService oafService;

    @Inject
    public SearcherRegistry(CrudService crudService, OafService oafService) {
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
        return String.format("photon.query.search.%sSearcher", name);
    }
}
