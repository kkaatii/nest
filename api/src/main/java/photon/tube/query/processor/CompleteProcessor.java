package photon.tube.query.processor;

import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;
import photon.tube.query.ArgumentClassMismatchException;
import photon.tube.query.GraphContainer;
import photon.tube.service.CrudService;

import java.util.ArrayList;
import java.util.List;

import static photon.util.Util.ensureList;

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
            return GraphContainer.fixateWith(ensureList(null), arrows);

        } catch (ClassCastException e) {
            throw new ArgumentClassMismatchException();
        }
    }
}
