package ch.hsr.ifs.iltis.core.core.collections.maps;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;


/**
 * A map from type ({@code Class<?extends TypeBound>}) to consumer for this type. Can be used to implement a crude version of dynamic dispatch using
 * method references.
 * 
 * @author tstauber
 *
 * @param <Bound>
 * The upper bound for the accepted types (exp. IASTNode)
 * 
 * @since 2.1
 */
public class TypeToConsumerMap<Bound> {

    private final TypeMap<Bound, Consumer<? extends Bound>> delegate = new TypeMap<Bound, Consumer<? extends Bound>>();

    @SuppressWarnings("unchecked")
    public <Type extends Bound> Consumer<Type> put(Class<Type> cls, Consumer<Type> consumer) {
        return (Consumer<Type>) delegate.put(cls, consumer);
    }

    @SuppressWarnings("unchecked")
    public <Type extends Bound> Consumer<Type> get(Class<Type> cls) {
        return (Consumer<Type>) delegate.get(cls);
    }

    @SuppressWarnings("unchecked")
    public <Type extends Bound> boolean accept(final Type value) {
        if (delegate.containsKey(value.getClass())) {
            get(((Class<Type>) value.getClass())).accept(value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tries to find the best matching consumer:
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
     * If a consumer was registered for {@code Inter}, and one for {@code B}, then {@code B.class} will match the consumer registered for {@code B},
     * and {@code A.class} will match the consumer registered for {@code Inter}.
     * 
     * @param <Type>
     * @param cls
     * @return
     */
    @SuppressWarnings("unchecked")
    public <Type extends Bound> Consumer<Type> getBestMatch(final Class<Type> cls) {
        return (Consumer<Type>) delegate.getBestMatch(cls);
    }

    @SuppressWarnings("unchecked")
    public <Type extends Bound> boolean acceptBestMatch(final Type value) {
        Consumer<? super Type> bestMatch = getBestMatch((Class<Type>) value.getClass());
        if (bestMatch == null) return false;
        bestMatch.accept(value);
        return true;
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    public Consumer<? extends Bound> remove(Object key) {
        return delegate.remove(key);
    }

    public void clear() {
        delegate.clear();
    }

    public Set<Class<? extends Bound>> keySet() {
        return delegate.keySet();
    }

    public Collection<Consumer<? extends Bound>> values() {
        return delegate.values();
    }

    public Set<Entry<Class<? extends Bound>, Consumer<? extends Bound>>> entrySet() {
        return delegate.entrySet();
    }

    public Consumer<? extends Bound> get(Object key) {
        return delegate.get(key);
    }

}
