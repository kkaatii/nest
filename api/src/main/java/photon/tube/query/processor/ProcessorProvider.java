package photon.tube.query.processor;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import photon.tube.auth.OafService;
import photon.tube.model.CrudService;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@Component
public final class ProcessorProvider {
    private static final Map<String, Processor> procMap = new HashMap<>();
    private final CrudService crudService;
    private final OafService oafService;

    @Autowired
    public ProcessorProvider(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
        //manuallyRegisterProcessors();
    }

    private void manuallyRegisterProcessors() {
        procMap.put("nrpattern", new PatternProcessor(crudService, oafService));
    }

    public Processor getProcessor(String abbrProcessorName) {
        return procMap.computeIfAbsent(abbrProcessorName, k -> {
            synchronized (procMap) {
                if (procMap.containsKey(abbrProcessorName)) {
                    return procMap.get(abbrProcessorName);
                }
                try {
                    Class<?> clazz = Class.forName(completeProcName(abbrProcessorName));
                    Constructor<?> ctor = clazz.getConstructor(CrudService.class, OafService.class);
                    return (Processor) ctor.newInstance(crudService, oafService);
                } catch (ClassNotFoundException ce) {
                    throw new ProcessorNotFoundException(ce);
                } catch (Exception e) {
                    throw new ProcessorInitFailedException(e);
                }
            }
        });
    }

    private static String completeProcName(String name) {
        return String.format("photon.tube.query.processor.%sProcessor", WordUtils.capitalizeFully(name));
    }
}
