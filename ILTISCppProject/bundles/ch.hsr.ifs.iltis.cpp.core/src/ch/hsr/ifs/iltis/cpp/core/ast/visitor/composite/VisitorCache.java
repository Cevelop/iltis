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

   public VisitorCache(int maximalCapacity) {
      listFactory = ignored -> new ArrayList<VisitorType>(maximalCapacity);
   }

   public List<VisitorType> getCache(Class<?> nodeType) {
      return cache.computeIfAbsent(nodeType, listFactory);
   }

   public void addCache(Class<?> nodeType, VisitorType visitor) {
      getCache(nodeType).add(visitor);
   }

   public void removeForAnyType(VisitorType visitor) {
      cache.values().stream().forEach(list -> list.remove(visitor));
   }

   public void removeAllForAnyType(Collection<VisitorType> visitors) {
      if (!visitors.isEmpty()) {
         cache.values().stream().forEach(list -> list.removeAll(visitors));
      }
   }

   public void remove(Class<?> nodeType, VisitorType visitor) {
      getCache(nodeType).remove(visitor);
   }
}
