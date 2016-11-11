package photon.tube.query.processor;

import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;
import photon.tube.query.QueryArgumentClassMismatchException;
import photon.tube.query.GraphContainer;
import photon.tube.service.CrudService;
import photon.tube.service.OafService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static photon.util.Util.ensureList;

// TODO add verification for node access

public class CompleteProcessor implements Processor {

    private final CrudService crudService;
    private final OafService oafService;

    public CompleteProcessor(CrudService crudService, OafService oafService) {
        this.crudService = crudService;
        this.oafService = oafService;
    }

    @Override
    public GraphContainer process(Integer ownerId, Object... args) throws QueryArgumentClassMismatchException {
        try {
            Integer[] ids = (Integer[]) args[0];
            int length = ids.length;
            if (length == 0) return GraphContainer.emptyContainer();

            ArrowType arrowType = (ArrowType) args[1];
            List<Arrow> arrows = new ArrayList<>();
            Arrow tmp;
            boolean[] accesses = new boolean[length];
            for (int i = 0; i < length; i++) {
                accesses[i] = oafService.authorizedRead(ownerId, crudService.getNodeFrame(ids[i]));
            }
            if (!oafService.authorizedRead(ownerId, crudService.getNodeFrame(ids[0])))
                return GraphContainer.emptyContainer();
            for (int i = 0; i < length; i++) {
                if (!accesses[i]) continue;
                for (int j = i + 1; j < length; j++) {
                    if (!accesses[j]) continue;
                    if (arrowType == ArrowType.ANY)
                        arrows.addAll(crudService.getAllArrowsBetween(ids[i], ids[j]));
                    else if ((tmp = crudService.getArrow(ids[i], arrowType, ids[j])) != null)
                        arrows.add(tmp);
                    else if ((tmp = crudService.getArrow(ids[i], arrowType.reverse(), ids[j])) != null)
                        arrows.add(tmp);
                }
            }
            return GraphContainer.fixateWith(ensureList(null), arrows);

        } catch (ClassCastException e) {
            throw new QueryArgumentClassMismatchException();
        }
    }
}
