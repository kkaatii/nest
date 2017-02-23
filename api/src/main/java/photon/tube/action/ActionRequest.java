package photon.tube.action;

import photon.tube.model.Owner;

import java.util.HashMap;
import java.util.Map;

// TODO differentiate attributes from params?
public class ActionRequest {
    public final Owner owner;
    public final Map<String, String> attributes;
    public Object[] params;

    public ActionRequest(Owner owner) {
        this.owner = owner;
        attributes = new HashMap<>();
    }
}
