package photon.tube.service;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.query.*;
import photon.tube.query.processor.Processor;
import photon.tube.query.processor.ProcessorNotFoundException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The core service as a proxy to process queries.<br>
 * After receiving a <tt>Query</tt>, it will find corresponding <tt>Processor</tt> to process it and cache the result <tt>GraphContainer</tt>
 * for the same query in the future.
 */
@Service
public class ProcessorBackedQueryService implements QueryService {

    private final Map<String, Processor> procMap = new HashMap<>();
    private final Map<QueryContext, GraphContainer> gcStore = new HashMap<>();

    private final CrudService crudService;
    private final AuthService authService;

    @Autowired
    public ProcessorBackedQueryService(CrudService crudService, AuthService authService) {
        this.crudService = crudService;
        this.authService = authService;
    }

    private QueryResult getResult(QueryContext context) {
        try {
            GraphContainer gc = gcStore.computeIfAbsent(context,
                    k -> findProcessor(context.type).process(context.owner, context.args));
            GraphContainer sectionContainer = context.sectionConfig.applyOn(gc);
            return new QueryResult(context)
                    .withGraphInfo(gc.info())
                    .withSectionInfo(sectionContainer.info())
                    .withSection(sectionContainer.export());
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
    }

    private Processor findProcessor(String procName) {
        return procMap.computeIfAbsent(procName, k -> {
            try {
                Class<?> clazz = Class.forName(completeProcName(procName));
                Constructor<?> ctor = clazz.getConstructor(CrudService.class, AuthService.class);
                return (Processor) ctor.newInstance(crudService, authService);
            } catch (Exception e) {
                throw new ProcessorNotFoundException(e);
            }
        });
    }

    private static String completeProcName(String name) {
        return String.format("photon.tube.query.processor.%sProcessor", WordUtils.capitalizeFully(name));
    }

    @Override
    public Query createQuery(QueryContext context) {
        return new Query(context) {
            @Override
            public QueryResult result() {
                return getResult(context);
            }
        };
    }

    @Override
    public Query createQuery(String string) {
        return new Query(new QueryContext.Builder().build()) {
            @Override
            public QueryResult result() {
                return null;
            }
        };
    }
}
