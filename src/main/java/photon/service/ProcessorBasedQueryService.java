package photon.service;

import photon.query.*;
import photon.query.processor.Processor;
import photon.query.processor.ProcessorNotFoundException;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The core service as a proxy to process queries.<br>
 * After receiving a execute, it will find corresponding <tt>Processor</tt> to process the execute then store returned <tt>GraphContainer</tt>
 * for next execute with the same <tt>qid</tt>
 */

@Service
public class ProcessorBasedQueryService implements QueryService {

    private final Map<Query, GraphContainer> gcStore = new HashMap<>();
    private final Map<String, Processor> procMap = new HashMap<>();

    private CrudService crudService;

    @Autowired
    public ProcessorBasedQueryService(CrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public QueryResult execute(Query query) {
        try {
            // Query qid = isNewQuery(query) ? generateQid(query.token) : query.qid;
            GraphContainer gc = gcStore.computeIfAbsent(query,
                    k -> getProcessor(query.type).process(query.args));
            return new QueryResult(query)
                    .withInfo(gc.info())
                    .withSlice(query.sliceConfig.applyOn(gc));
        } catch (Exception e) {
            throw new FailedQueryException(e);
        }
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
        return String.format("photon.query.processor.%sProcessor", WordUtils.capitalizeFully(name));
    }

    /*
    private static boolean isNewQuery(Query qc) {
        if (qc.qid == null)
            return true;
        if (qc.qid.startsWith(DigestUtils.sha1Hex(qc.token), 2))
            return false;
        throw new RuntimeException("Qid and token do not match!");
    }
*/

    /*@Override
    public String generateToken(Query q) {

        if (q.getToken() != null && validToken(q.getToken())) return q.getToken();

        String qid;
        do {
            qid = randomString(TOKEN_LENGTH);
        } while (gcStore.containsKey(qid));
        q.setToken(qid);
        return qid;
    }

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom RNG = new SecureRandom();
    private static final int TOKEN_LENGTH = 16;

    private static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(RNG.nextInt(AB.length())));
        return sb.toString();
    }

    private boolean validToken(String token) {
        return true;
    }*/
}
