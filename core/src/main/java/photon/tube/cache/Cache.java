package photon.tube.cache;

import org.springframework.stereotype.Service;

@Service
public interface Cache<K, V> {
    V get(K key);
    boolean put(K key, V value);
}
