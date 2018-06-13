package ch.hsr.ifs.iltis.cpp.core.ast.visitor.composite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;

import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor;


public class VisitorCache {

   /**
    * The initialization number is taken from the count of visit methods in an ASTVisitor.
    */
   private final IdentityHashMap<Class<?>, ArrayList<SimpleVisitor<?>>> cache = new IdentityHashMap<>(30);
   private final Function<Class<?>, ArrayList<SimpleVisitor<?>>>        listFactory;

   public VisitorCache(int maximalCapacity) {
      listFactory = ignored -> new ArrayList<SimpleVisitor<?>>(maximalCapacity);
   }

   public List<SimpleVisitor<?>> getCache(Class<?> nodeType) {
      return cache.computeIfAbsent(nodeType, listFactory);
   }

   public void addCache(Class<?> nodeType, SimpleVisitor<?> visitor) {
      getCache(nodeType).add(visitor);
   }

   public void removeForAnyType(SimpleVisitor<?> visitor) {
      cache.values().stream().forEach(list -> list.remove(visitor));
   }

   public void removeAllForAnyType(Collection<SimpleVisitor<?>> visitors) {
      if (!visitors.isEmpty()) {
         cache.values().stream().forEach(list -> list.removeAll(visitors));
      }
   }

   public void remove(Class<?> nodeType, SimpleVisitor<?> visitor) {
      getCache(nodeType).remove(visitor);
   }
}
