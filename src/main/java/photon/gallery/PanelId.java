package photon.gallery;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dan on 07/10/2016.
 */
public class PanelId {
    private Map<String, String> map;

    public PanelId() {
        map = new HashMap<>();
    }

    public void put(String idName, String idValue) {
        map.put(idName, idValue);
    }

    public String get(String idName) {
        return map.get(idName);
    }
}
