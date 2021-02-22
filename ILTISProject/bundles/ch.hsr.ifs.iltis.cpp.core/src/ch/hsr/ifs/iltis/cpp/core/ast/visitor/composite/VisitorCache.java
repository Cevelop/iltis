package ch.hsr.ifs.iltis.cpp.core.ast.visitor.composite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;


public class VisitorCache<VisitorType> {

    /**
     * The initialization number is taken from the count of visit methods in an ASTVisitor.
     */
    private final IdentityHashMap<Class<?>, ArrayList<VisitorType>> cache = new IdentityHashMap<>(30);
    private final Function<Class<?>, ArrayList<VisitorType>>        listFactory;

    public VisitorCache(final int maximalCapacity) {
        listFactory = ignored -> new ArrayList<>(maximalCapacity);
    }

    public List<VisitorType> getCache(final Class<?> nodeType) {
        return cache.computeIfAbsent(nodeType, listFactory);
    }

    public void addCache(final Class<?> nodeType, final VisitorType visitor) {
        getCache(nodeType).add(visitor);
    }

    public void removeForAnyType(final VisitorType visitor) {
        cache.values().stream().forEach(list -> list.remove(visitor));
    }

    public void removeAllForAnyType(final Collection<VisitorType> visitors) {
        if (!visitors.isEmpty()) {
            cache.values().stream().forEach(list -> list.removeAll(visitors));
        }
    }

    public void remove(final Class<?> nodeType, final VisitorType visitor) {
        getCache(nodeType).remove(visitor);
    }
}
