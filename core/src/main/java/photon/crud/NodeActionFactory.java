package photon.crud;

import photon.action.*;
import photon.model.Node;
import photon.model.Owner;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static photon.query.Conventions.DICT_KEY_NODE;
import static photon.query.Conventions.DICT_KEY_OWNER;

public class NodeActionFactory extends ActionFactory<Generator<Collection<Node>>> {
    private static final String actionName = "node";
    private final CrudService crudService;
    private final OafService oafService;

    @Inject
    public NodeActionFactory(CrudService crudService, OafService oafService) {
        super(actionName);
        this.crudService = crudService;
        this.oafService = oafService;
    }

    @Override
    public Generator<Collection<Node>> createAction(ActionRequest actionRequest) {
        Node reqNode = actionRequest.get(DICT_KEY_NODE, Node.class);
        Owner owner = actionRequest.get(DICT_KEY_OWNER, Owner.class);
        List<Node> nodes = new ArrayList<>();
        /*
         * Read a Node
         */
        if (reqNode.getType() == null) {
            return Transformation.of(() -> {
                Integer id = reqNode.getId();
                if (id == null) {
                    throw new IllegalArgumentException("Node ID not designated");
                }
                Node node = crudService.getNode(id);
                if (oafService.authorized(AccessLevel.READ, owner, node.getFrame()))
                    nodes.add(node);
                return nodes;
            });
        }
        /*
         * Create/update a Node
         */
        else {
            return Transformation.of(() -> {
                Integer id = reqNode.getId();
                Node node = id == null ? null : crudService.getNode(id);
                if ((id != null && node == null) // Wrong ID
                        || !oafService.authorized(AccessLevel.WRITE, owner, reqNode.getFrame()) // Writing to the new Frame not allowed
                        || (node != null && !oafService.authorized(AccessLevel.WRITE, owner, node.getFrame()))) // Writing to the original frame not allowed
                    return nodes;
                if (id == null)
                    node = crudService.putNode(reqNode);
                else
                    node = crudService.updateNode(reqNode);
                nodes.add(node);
                return nodes;
            });
        }

//        return null;
    }
}
