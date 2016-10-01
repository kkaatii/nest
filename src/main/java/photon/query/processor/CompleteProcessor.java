package photon.query.processor;

import photon.data.Arrow;
import photon.data.ArrowType;
import photon.query.ArgumentClassMismatchException;
import photon.query.GraphContainer;
import photon.service.CrudService;

import static photon.util.Util.ensureList;

import java.util.*;
import java.util.stream.Collectors;

public class CompleteProcessor implements Processor {

    private CrudService crudService;

    public CompleteProcessor(CrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public GraphContainer process(Object... args) throws ArgumentClassMismatchException {
        try {
            int[] ids = (int[]) args[0];
            ArrowType arrowType = (ArrowType) args[1];

            List<Arrow> arrows = new ArrayList<>();
            Arrow tmp;
            for (int i = 0; i < ids.length; i++) {
                for (int j = i + 1; j < ids.length; j++) {
                    if (arrowType == ArrowType.UNSPECIFIED)
                        arrows.addAll(crudService.getAllArrowsBetween(ids[i], ids[j]));
                    else if ((tmp = crudService.getArrow(ids[i], arrowType, ids[j])) != null)
                        arrows.add(tmp);
                    else if ((tmp = crudService.getArrow(ids[i], arrowType.reverse(), ids[j])) != null)
                        arrows.add(tmp);
                }
            }
            Set<Integer> extIdSet = arrows.stream()
                    .filter(Arrow::hasExtension)
                    .map(Arrow::getExtension)
                    .collect(Collectors.toSet());
            return GraphContainer.fixateWith(ensureList(null), arrows, crudService.getExtensions(extIdSet));

        } catch (ClassCastException e) {
            throw new ArgumentClassMismatchException();
        }
    }
}
