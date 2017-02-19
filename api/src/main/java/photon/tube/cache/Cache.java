package photon.tube.cache;

import org.springframework.stereotype.Service;

/**
 * Created by Dun Liu on 2/7/2017.
 */
@Service
public interface Cache<K, V> {
    public V get(K key);
    public boolean put(K key, V value);
}
