package photon.search;

import photon.action.ActionFactory;
import photon.action.ActionRequest;
import photon.action.Generator;
import photon.action.Transformation;
import photon.crud.OafService;
import photon.crud.CrudService;
import photon.model.Owner;
import photon.query.GraphContainer;

import javax.inject.Inject;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static photon.query.Conventions.DICT_KEY_OWNER;

public class SearchActionFactory extends ActionFactory<Generator<GraphContainer>> {
    private static final String actionName = "search";
    private static final ConcurrentMap<String, Searcher> SEARCHER_MAP = new ConcurrentHashMap<>();

    private final CrudService crudService;
    private final OafService oafService;

    @Inject
    public SearchActionFactory(CrudService crudService, OafService oafService) {
        super(actionName);
        this.crudService = crudService;
        this.oafService = oafService;
    }

    private Searcher findSearcher(String abbrSearcherName) {
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
        return String.format("photon.search.%sSearcher", name);
    }

    @Override
    public Generator<GraphContainer> createAction(ActionRequest request) {
        String searcherName = request.get("searcher", String.class);
        return Transformation.of(
                () -> findSearcher(searcherName).search(request.get(DICT_KEY_OWNER, Owner.class), request)
        );
    }

}
