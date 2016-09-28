package nest.service;

import nest.query.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * The core service as a proxy to process queries.<br>
 * After receiving a query, it will find corresponding <tt>Processor</tt> to process the query then store returned <tt>GraphContainer</tt>
 * for next query with the same <tt>qid</tt>
 */
@Service
public class ProcessorQueryService implements QueryService {

    private final Map<String, GraphContainer> gcStore = new HashMap<>();
    private final Map<String, Processor> procMap = new HashMap<>();

    private CrudService crudService;

    @Autowired
    public ProcessorQueryService(CrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public QueryResult query(QueryContext context) {
        try {
            String qid = isNewQuery(context) ? generateQid(context.token) : context.qid;
            GraphContainer gc = gcStore.computeIfAbsent(qid,
                    k -> getProcessor(context.processorName).process(context.args));
            return new QueryResult(qid)
                    .withInfo(gc.info())
                    .withSlice(context.sliceParam.applyOn(gc));
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
    }

    private static boolean isNewQuery(QueryContext qc) {
        if (qc.qid == null)
            return true;
        if (qc.qid.startsWith(DigestUtils.sha1Hex(qc.token), 2))
            return false;
        throw new RuntimeException("Qid and token do not match!");
    }

    private String generateQid(String token) {
        String qid;
        do {
            qid = randomString(QID_LENGTH);
        } while (gcStore.containsKey(qid));
        return qid;
    }

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private static final int QID_LENGTH = 16;

    private static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private Processor getProcessor(String name) {
        return procMap.computeIfAbsent(name, k -> {
            try {
                Class<?> clazz = Class.forName(completeProcessorName(name));
                Constructor<?> ctor = clazz.getConstructor(CrudService.class);
                return (Processor) ctor.newInstance(crudService);
            } catch (Exception e) {
                throw new ProcessorNotFoundException(e);
            }
        });
    }

    private static String completeProcessorName(String name) {
        return String.format("nest.query.processor.%sProcessor", WordUtils.capitalizeFully(name));
    }
}
