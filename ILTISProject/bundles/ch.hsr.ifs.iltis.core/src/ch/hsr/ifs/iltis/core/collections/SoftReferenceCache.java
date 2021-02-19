package ch.hsr.ifs.iltis.core.collections;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;


/**
 * A soft reference based cache. It uses the phantoms of the reference to remove the entries from the map once the soft-references were collected.
 *
 * @author tstauber
 *
 * @param <KeyType>
 * The reference cache key type
 * @param <ValueType>
 * The reference cache value type
 * 
 * @since 2.0
 */
public class SoftReferenceCache<KeyType, ValueType> implements Map<KeyType, ValueType> {

    protected MutableMap<KeyType, CacheRef<KeyType, ValueType>> cacheMap = Maps.mutable.empty();
    protected ReferenceQueue<ValueType>                         refQueue = new ReferenceQueue<>();

    @Override
    public ValueType get(final Object key) {
        handleEnqueuedCacheRefs();
        final CacheRef<KeyType, ValueType> cRef = cacheMap.get(key);
        return cRef == null ? null : cRef.get();
    }

    @Override
    public boolean containsKey(final Object key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return cacheMap.containsValue(value);
    }

    @Override
    public int size() {
        handleEnqueuedCacheRefs();
        return cacheMap.size();
    }

    @Override
    public boolean isEmpty() {
        handleEnqueuedCacheRefs();
        return cacheMap.isEmpty();
    }

    @Override
    public ValueType put(final KeyType key, final ValueType value) {
        handleEnqueuedCacheRefs();
        final CacheRef<KeyType, ValueType> oldCRef = cacheMap.put(key, new CacheRef<>(key, value, refQueue));
        return oldCRef == null ? null : oldCRef.get();
    }

    @Override
    public ValueType remove(final Object key) {
        handleEnqueuedCacheRefs();
        final CacheRef<KeyType, ValueType> cRef = cacheMap.remove(key);
        return cRef == null ? null : cRef.get();
    }

    @Override
    public void putAll(final Map<? extends KeyType, ? extends ValueType> map) {
        handleEnqueuedCacheRefs();
        for (final KeyType key : map.keySet()) {
            cacheMap.put(key, new CacheRef<>(key, map.get(key), refQueue));
        }
    }

    @Override
    public void clear() {
        handleEnqueuedCacheRefs();
        cacheMap.clear();
    }

    @Override
    public Set<KeyType> keySet() {
        return cacheMap.keySet();
    }

    /**
     * Calling this will create strong references to the entries, resulting in non-collectable objects.
     */
    @Override
    public Collection<ValueType> values() {
        return cacheMap.valuesView().collect(cr -> cr.get(), Lists.mutable.empty());
    }

    /**
     * Calling this will create strong references to the entries, resulting in non-collectable objects.
     */
    @Override
    public Set<Entry<KeyType, ValueType>> entrySet() {
        return cacheMap.keyValuesView().collect(kvp -> new DecachingEntry<>(kvp.getOne(), kvp.getTwo(), new WeakReference<>(this)), Sets.mutable
                .empty());
    }

    /* -------------- CACHE HANDLING -------------- */

    /**
     * Pull phantoms and use them to remove the corresponding entry from the map
     */
    @SuppressWarnings("unchecked")
    private void handleEnqueuedCacheRefs() {
        for (Reference<? extends ValueType> phantom = refQueue.poll(); phantom != null; phantom = refQueue.poll()) {
            cacheMap.remove(((CacheRef<KeyType, ValueType>) phantom).key);
        }
    }

    /**
     * A kind of SoftReference holding the key corresponding to a value in the cache.
     *
     * @author tstauber
     *
     * @param <KeyType>
     * The reference cache key type
     * @param <ValueType>
     * The reference cache value type
     */
    protected static class CacheRef<KeyType, ValueType> extends SoftReference<ValueType> {

        protected final KeyType key;

        public CacheRef(final KeyType key, final ValueType ref, final ReferenceQueue<ValueType> queue) {
            super(ref, queue);
            this.key = key;
        }

    }

    /**
     * A map entry used for memory safe decaching of SoftReferenceCache.
     *
     * @author tstauber
     *
     * @param <KeyType>
     * The reference cache key type
     * @param <ValueType>
     * The reference cache value type
     */
    protected static class DecachingEntry<KeyType, ValueType> implements Map.Entry<KeyType, ValueType> {

        protected KeyType   key;
        protected ValueType value;

        private final WeakReference<SoftReferenceCache<KeyType, ValueType>> writeThroughHolder;

        public DecachingEntry(final KeyType key, final CacheRef<KeyType, ValueType> cacheRef,
                              final WeakReference<SoftReferenceCache<KeyType, ValueType>> cacheHolder) {
            this.key = key;
            this.value = cacheRef.get();
            this.writeThroughHolder = cacheHolder;
        }

        @Override
        public KeyType getKey() {
            return key;
        }

        @Override
        public ValueType getValue() {
            return value;
        }

        @Override
        public ValueType setValue(final ValueType value) {
            this.value = value;
            final SoftReferenceCache<KeyType, ValueType> cacheHolder = writeThroughHolder.get();
            if (cacheHolder != null && cacheHolder.cacheMap.containsKey(this.key)) {
                return cacheHolder.put(this.key, this.value);
            }
            return null;
        }

    }
}
