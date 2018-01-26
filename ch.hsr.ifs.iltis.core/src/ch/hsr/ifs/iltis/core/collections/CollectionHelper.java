package ch.hsr.ifs.iltis.core.collections;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import ch.hsr.ifs.iltis.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.functional.Functional;


public abstract class CollectionHelper {

   @SafeVarargs
   public static <T> T[] array(final T... elements) {
      return elements;
   }

   @SafeVarargs
   public static <T> List<T> list(final T... elements) {
      return new ArrayList<>(asList(elements));
   }

   public static <T> List<T> list(final Collection<T> elements) {
      return new ArrayList<>(elements);
   }

   @SafeVarargs
   public static <T> Set<T> unorderedSet(final T... elements) {
      return new HashSet<>(asList(elements));
   }

   public static <T> Set<T> unorderedSet() {
      return new HashSet<>();
   }

   public static <T> Set<T> orderPreservingSet() {
      return new LinkedHashSet<>();
   }

   @SafeVarargs
   public static <T> Set<T> orderPreservingSet(final T... elements) {
      return new LinkedHashSet<>(asList(elements));
   }

   public static <T> Set<T> orderPreservingSet(final Collection<T> elements) {
      return new LinkedHashSet<>(elements);
   }

   public static <K, V> Map<K, V> orderPreservingMap() {
      return new LinkedHashMap<>();
   }

   public static <K, V> Map<K, V> orderedMap() {
      return new TreeMap<>();
   }

   public static <K, V> Map<K, V> unorderedMap() {
      return new HashMap<>();
   }

   public static <K, V> Map<K, V> zipMap(final K[] keys, final V[] values) {
      final HashMap<K, V> map = new HashMap<>();

      for (int i = 0; i < keys.length; i++) {
         map.put(keys[i], values[i]);
      }
      return map;
   }

   public static <T> Collection<T> checkedCast(final Collection<?> list, final Class<T> clazz) {
      return list.stream().map((it) -> clazz.cast(it)).collect(Collectors.toList());
   }

   public static <T> Map<T, T> checkedCast(final Map<?, ?> map, final Class<T> clazz) {
      return Functional.zip(map).map((pair) -> pair.cast(clazz, clazz)).collect(Collectors.toMap((pair) -> pair.first(), (pair) -> pair.second()));
   }

   public static <T> boolean notNull(final Iterable<T> it) {
      ILTISException.Unless.notNull(it, "iterable must not be null");

      for (final T e : it) {
         if (e == null) {
            return false;
         }
      }

      return true;
   }

   public static boolean isEmpty(final Iterable<?> it) {
      return !it.iterator().hasNext();
   }

   public static <E> Optional<E> head(final Iterable<E> it) {
      final Iterator<E> iterator = it.iterator();
      return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
   }

   public static <E> Collection<E> tail(final Iterable<E> it) {
      final List<E> l = new ArrayList<>();
      final Iterator<E> i = it.iterator();
      if (i.hasNext()) {
         i.next();
         while (i.hasNext()) {
            l.add(i.next());
         }
      }
      return l;
   }

   public static <E> E head(final Iterable<E> it, final E defaultValue) {
      return StreamSupport.stream(it.spliterator(), false).findFirst().orElse(defaultValue);
   }

   public static <E> Optional<E> last(final E[] elements) {
      if (elements.length < 1) {
         return Optional.empty();
      }
      return Optional.of(elements[elements.length - 1]);
   }

   public static <E> Optional<E> last(final List<E> elements) {
      if (elements.isEmpty()) {
         return Optional.empty();
      }
      return Optional.of(elements.get(elements.size() - 1));
   }

   public static <E> boolean haveSameElementsInSameOrder(final Collection<E> c1, final Collection<E> c2) {
      final Iterator<E> it = c2.iterator();
      for (final E e : c1) {
         if (!e.equals(it.next())) {
            return false;
         }
      }
      return true;
   }
}
