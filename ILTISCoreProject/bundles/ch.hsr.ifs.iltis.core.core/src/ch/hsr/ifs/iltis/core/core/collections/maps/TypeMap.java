package ch.hsr.ifs.iltis.core.core.collections.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;


/**
 * A map from type ({@code Class<?extends TypeBound>}) to value ({@code ValueType})
 * 
 * @author tstauber
 *
 * @param <TypeBound>
 * The upper bound for the type (exp. IASTNode)
 * @param <ValueType>
 * The value-type used.
 * 
 * @since 2.1
 */
public class TypeMap<TypeBound, ValueType extends Object> implements Map<Class<? extends TypeBound>, ValueType> {

    private transient final HashMap<Class<? extends TypeBound>, ValueType> delegate;

    public TypeMap() {
        delegate = new HashMap<>();
    }

    public TypeMap(int initialSize) {
        delegate = new HashMap<>(initialSize);
    }

    @Override
    /**
     * @inheritDoc
     */
    public ValueType get(final Object key) {
        if (!(key instanceof Class<?>)) return null;
        return delegate.get(key);
    }

    /**
     * Tries to find the value for the closest matching type:
     * 
     * <pre>
     * interface Inter {}
     *
     * 
     * 
     * class A implements Inter {}
     * 
     * 
     * 
     * class B implements Inter {}
     * </pre>
     * 
     * If a value was registered for {@code Inter}, and one for {@code B}, then {@code B.class} will match the value registered for {@code B},
     * and {@code A.class} will match the value registered for {@code Inter}.
     * 
     * @param <Type>
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public ValueType getBestMatch(final Object key) {
        if (!(key instanceof Class<?>)) return null;
        Class<? extends TypeBound> cls = (Class<? extends TypeBound>) key;
        for (Pair<Class<?>, List<Class<?>>> sts = Tuples.pair(cls, Arrays.asList(cls.getInterfaces())); sts.getOne() != null && !sts.getTwo()
                .isEmpty(); sts = getNextSuperTypes(sts)) {
            /* Test for this class */
            if (sts.getOne() != null && delegate.containsKey(sts.getOne())) return get(sts.getOne());
            /* Test for interfaces (implemented by cls, and super types of previous interfaces */
            for (Class<?> it : sts.getTwo()) {
                if (delegate.containsKey(it)) return get(it);
            }
        }
        return null;
    }

    private Pair<Class<?>, List<Class<?>>> getNextSuperTypes(Pair<Class<?>, List<Class<?>>> sts) {
        Class<?> clazz = sts.getOne() == null ? null : sts.getOne().getSuperclass();
        List<Class<?>> list = new ArrayList<>();
        if (clazz != null) {
            /* Add interfaces from the the superclass */
            Collections.addAll(list, clazz.getInterfaces());
        }
        /* Add super interfaces of current interfaces */
        list.addAll(sts.getTwo().stream().flatMap(ic -> Stream.of(ic.getInterfaces())).collect(Collectors.toList()));
        return Tuples.pair(clazz, list);
    }

    /**
     * A getter returning values for super-types of the given key as well.
     * 
     * @param key
     * The class for which to get the value.
     * @return {@link Collections#EMPTY_LIST} if key is null or not a class, else a list of all values found for keys that are assignable from the
     * given key.
     */
    public List<ValueType> getSuper(final Object key) {
        if (!(key instanceof Class<?>)) return Collections.emptyList();
        return keySet().stream().filter(c -> c.isAssignableFrom((Class<?>) key)).map(c -> delegate.get(c)).collect(Collectors.toList());
    }

    /**
     * A getter returning values for super-types of the given key as well.
     * 
     * @param key
     * The class for which to get the value.
     * @return {@link Collections#EMPTY_LIST} if key is null or not a class, else a list of all values found for keys that are assignable from the
     * given key.
     */
    public List<ValueType> getSub(final Object key) {
        if (!(key instanceof Class<?>)) return null;
        return keySet().stream().filter(c -> ((Class<?>) key).isAssignableFrom(c)).map(c -> delegate.get(c)).collect(Collectors.toList());
    }

    @Override
    public ValueType put(Class<? extends TypeBound> key, ValueType value) {
        return delegate.put(key, value);
    }

    @Override
    public ValueType remove(Object key) {
        return delegate.remove(key);
    }

    @Override
    public Set<Entry<Class<? extends TypeBound>, ValueType>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public void putAll(Map<? extends Class<? extends TypeBound>, ? extends ValueType> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<Class<? extends TypeBound>> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<ValueType> values() {
        return delegate.values();
    }

}
