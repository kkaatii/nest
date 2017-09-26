package photon.tube.action;

import photon.util.GenericDict;

import java.util.Map;

/**
 * Representation of parsed json request. <code>ActionFactory.createAction()</code> accepts an <code>ActionRequest</code>
 * and produces an <code>Action</code>. Note: only when the <code>ActionFactory</code> and the <code>ActionRequest</code>
 * have identical <code>actionName</code> will <code>createAction()</code> have proper result.
 */
public class ActionRequest extends GenericDict {

    private String actionName;

    public ActionRequest() {}

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String actionName() {
        return actionName;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Action: ").append(actionName());

        for (Map.Entry<String, ?> entry : values()) {
            Object value = entry.getValue();
            if (!value.getClass().isArray()) {
                sb.append(String.format("\nArgument \"%s\": %s", entry.getKey(), value));
            } else {
                StringBuilder sba = new StringBuilder();
                if (value instanceof int[]) {
                    for (int i : (int[]) value)
                        sba.append(", ").append(i);
                } else if (value instanceof double[]) {
                    for (double d : (double[]) value)
                        sba.append(", ").append(d);
                } else {
                    for (Object o : (Object[]) value)
                        sba.append(", ").append(o);
                }
                sba.replace(0, 2, "[");
                sba.append("]");
                sb.append(String.format("\nArgument \"%s\": %s", entry.getKey(), sba));
            }
        }
        return sb.toString();
    }

}
