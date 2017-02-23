package photon.tube.action.searcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import photon.tube.auth.OafService;
import photon.tube.model.CrudService;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@Component
public final class SearcherFactory {
    private static final Map<String, Searcher> procMap = new HashMap<>();
    private final CrudService crudService;
    private final OafService oafService;

    @Autowired
    public SearcherFactory(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
        //manuallyRegisterProcessors();
    }

    public Searcher findSearcher(String abbrSearcherName) {
        return procMap.computeIfAbsent(abbrSearcherName, k -> {
            synchronized (procMap) {
                if (procMap.containsKey(abbrSearcherName)) {
                    return procMap.get(abbrSearcherName);
                }
                try {
                    Class<?> clazz = Class.forName(completeName(abbrSearcherName));
                    Constructor<?> ctor = clazz.getConstructor(CrudService.class, OafService.class);
                    return (Searcher) ctor.newInstance(crudService, oafService);
                } catch (ClassNotFoundException ce) {
                    throw new SearcherNotFoundException(ce);
                } catch (Exception e) {
                    throw new SearcherInitFailedException(e);
                }
            }
        });
    }

    private static String completeName(String name) {
        return String.format("photon.tube.action.searcher.%sSearcher", name);
    }
}
