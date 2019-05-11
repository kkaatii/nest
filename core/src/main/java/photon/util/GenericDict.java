package photon.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Dictionary which supports arbitrary value type. Needs to specify value type when storing and retrieving value.
 */
public class GenericDict {
    private Map<Class<?>, Map<String, Object>> store = new HashMap<>();

    public <T> void put(String key, Class<T> type, T entry) {
        Map<String, Object> shelf = store.computeIfAbsent(type, k -> new HashMap<>());
        shelf.put(key, entry);
    }

    public <T> T get(String key, Class<T> type) {
        Map<String, Object> shelf = store.get(type);
        return shelf == null ? null : type.cast(shelf.get(key));
    }

    public Collection<Map.Entry<String, ?>> values() {
        if (store.isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return store.values().stream().flatMap(m->m.entrySet().stream()).collect(Collectors.toList());
        }
    }
}
