package photon.tube.query.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.query.*;

import java.util.HashMap;
import java.util.Map;

/**
 * The core service as a proxy to process queries.<br>
 * After receiving a <tt>Query</tt>, it will find corresponding <tt>Processor</tt> to process it and cache the result <tt>GraphContainer</tt>
 * for the same query in the future.
 */
@Service
public class ProcessorBackedQueryService implements QueryService {

    private final Map<QueryContext, GraphContainer> gcStore = new HashMap<>();
    private final ProcessorProvider provider;

    @Autowired
    public ProcessorBackedQueryService(ProcessorProvider provider) {
        this.provider = provider;
    }

    private QueryResult processContext(QueryContext context) {
        try {
            GraphContainer graphContainer = gcStore.computeIfAbsent(
                    context,
                    ctx -> provider.getProcessor(ctx.handler).process(ctx.owner, ctx.args)
            );
            GraphContainer sectionContainer = context.sectionConfig.applyOn(graphContainer);
            return new QueryResult(context, graphContainer.info(), sectionContainer.info(), sectionContainer.export());
        } catch (UnauthorizedActionException uae) {
            throw uae;
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
    }

    @Override
    public Query createQuery(QueryContext context) {
        return new Query(context) {
            private QueryResult result;

            @Override
            public QueryResult result() {
                if (result == null) result = processContext(context);
                return result;
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
