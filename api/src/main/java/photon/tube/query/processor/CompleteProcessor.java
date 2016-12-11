package photon.tube.query.processor;

import photon.tube.auth.AuthService;
import photon.tube.auth.UnauthorizedActionException;
import photon.tube.model.Arrow;
import photon.tube.model.ArrowType;
import photon.tube.model.CrudService;
import photon.tube.model.Owner;
import photon.tube.query.GraphContainer;

import java.util.ArrayList;
import java.util.List;

import static photon.util.Util.ensureList;

public class CompleteProcessor extends Processor {

    public CompleteProcessor(CrudService crudService, AuthService authService) {
        super(crudService, authService);
    }

    @Override
    public GraphContainer process(Owner owner, Object... args)
            throws QueryArgumentClassException, UnauthorizedActionException {
        try {
            Integer[] ids = (Integer[]) args[0];
            int length = ids.length;
            if (length == 0) return GraphContainer.emptyContainer();

            ArrowType arrowType = (ArrowType) args[1];
            List<Arrow> arrows = new ArrayList<>();
            Arrow tmp;
            for (Integer id : ids) {
                if (!authService.authorizedRead(owner, crudService.getNodeFrame(id)))
                    throw new UnauthorizedActionException();
            }
            for (int i = 0; i < length; i++) {
                for (int j = i + 1; j < length; j++) {
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
            throw new QueryArgumentClassException();
        }
    }
}
