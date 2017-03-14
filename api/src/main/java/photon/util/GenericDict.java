package photon.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dun Liu on 3/4/2017.
 */
public class GenericDict {
    private Map<Class<?>, Map<String, Object>> store = new HashMap<>();

    public <T> void put(Class<T> type, String key, T entry) {
        Map<String, Object> shelf = store.computeIfAbsent(type, k -> new HashMap<>());
        shelf.put(key, entry);
    }

    public <T> T get(Class<T> type, String key) {
        Map<String, Object> shelf = store.get(type);
        return shelf == null ? null : type.cast(shelf.get(key));

    }
}
