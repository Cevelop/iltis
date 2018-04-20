package ch.hsr.ifs.iltis.core.core.collections;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ch.hsr.ifs.iltis.core.core.data.AbstractPair;
import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.core.functional.Functional;
import ch.hsr.ifs.iltis.core.core.functional.StreamFactory;
import ch.hsr.ifs.iltis.core.core.functional.StreamUtil;
import ch.hsr.ifs.iltis.core.core.functional.functions.Equals;


public abstract class CollectionUtil {

   /**
    * Used for implicit creation of an array by using varargs
    * 
    * @param elements
    *        The elements from which to create the array
    * @return an array consisting of the passed elements
    */
   @SafeVarargs
   public static <T> T[] array(final T... elements) {
      return elements;
   }

   /**
    * Used for implicit creation of a List from varargs
    * 
    * @param elements
    *        The elements from which to create the list
    * @return A {@link List}
    */
   @SafeVarargs
   public static <T> List<T> list(final T... elements) {
      return new ArrayList<>(asList(elements));
   }

   /**
    * Used for implicit creation of a Map from arrays
    * 
    * @param elements
    *        The elements from which to create the list
    * @return A {@link Map}
    */
   public static <K, V> Map<K, V> map(final K[] keys, final V[] values) {
      return Functional.zip(keys, values).collect(StreamUtil.toNullableMap());
   }

   /**
    * Used to convert a {@link Collection} to a {@link List}
    */
   public static <T> List<T> list(final Collection<T> elements) {
      return new ArrayList<>(elements);
   }

   /**
    * Used to create an unordered set from varargs
    * 
    * @param elements
    *        The elements from which to create the set
    * @return A {@link Set} containing the elements
    */
   @SafeVarargs
   public static <T> Set<T> unorderedSet(final T... elements) {
      return new HashSet<>(asList(elements));
   }

   /**
    * Used to create an ordered set from varargs
    * 
    * @param elements
    *        The elements from which to create the set
    * @return A {@link Set} containing the elements
    */
   @SafeVarargs
   public static <T> Set<T> orderPreservingSet(final T... elements) {
      return new LinkedHashSet<>(asList(elements));
   }

   /**
    * Used to create an ordered set from a {@link Collection}
    * 
    * @param elements
    *        The elements from which to create the set
    * @return A {@link Set} containing the elements
    */
   public static <T> Set<T> orderPreservingSet(final Collection<T> elements) {
      return new LinkedHashSet<>(elements);
   }

   /**
    * Used to create a {@link Map} from two arrays.
    * 
    * The arrays should be of the same length, else null will be used as default value.
    * 
    * @param keys
    *        The array with the keys for the map
    * @param values
    *        The array with the values for the map
    * @return A {@link Map}
    */
   public static <K, V> Map<K, V> zipMap(final K[] keys, final V[] values) {
      return Functional.zip(keys, values).collect(StreamUtil.toMap());
   }

   /**
    * Used to cast a {@link Collection} of one type to a Collection of the type passed as clazz
    * 
    * @param list
    *        The Collection to cast
    * @param clazz
    *        The target Class
    * @return A Collection of the type of clazz
    * @throws ClassCastException
    *         If the cast is invalid
    */
   public static <T> Collection<T> checkedCast(final Collection<?> list, final Class<T> clazz) {
      return list.stream().map((it) -> clazz.cast(it)).collect(Collectors.toList());
   }

   /**
    * Used to cast a {@link map} of one type to a Map with keys and values of the type passed as clazz
    * 
    * @param list
    *        The Map to cast
    * @param clazz
    *        The target Class
    * @return A Map of the type of clazz
    * @throws ClassCastException
    *         If the cast is invalid
    */
   public static <T> Map<T, T> checkedCast(final Map<?, ?> map, final Class<T> clazz) {
      return checkedCast(map, clazz, clazz);
   }

   /**
    * Used to cast a {@link map} to a Map with keys of type keyClazz and values of valueClazz
    * 
    * @param keyClazz
    *        The target Class for the keys
    * @param valueClazz
    *        The target Class for the values
    * @return A Map<keyClazz, valueClazz>
    * @throws ClassCastException
    *         If the cast is invalid
    */
   public static <T1, T2> Map<T1, T2> checkedCast(final Map<?, ?> map, final Class<T1> keyClazz, final Class<T2> valueClazz) {
      return Functional.zip(map).map((pair) -> pair.cast(keyClazz, valueClazz)).collect(StreamUtil.toMap());
   }

   /**
    * Checks if the {@link Iterable} it has no {@code null} values
    * 
    * @param it
    *        The Iterable to check
    * @return {@code true} if none of the elements of it are {@code null}
    */
   public static <T> boolean notNull(final Iterable<T> it) {
      ILTISException.Unless.notNull("iterable must not be null", it);
      for (final T e : it) {
         if (e == null) { return false; }
      }
      return true;
   }

   /**
    * Used to find if an {@link Iterable} is empty
    * 
    * @param it
    *        The Iterable to check
    * @return {@code true} if it is empty
    */
   public static boolean isEmpty(final Iterable<?> it) {
      return !it.iterator().hasNext();
   }

   /**
    * Used to obtain the first element of any {@link Iterable}
    * 
    * @param it
    *        The Iterable
    * @return {@link Optional} containing the first element, or an empty Optional if the iterable is empty
    */
   public static <E> Optional<E> head(final Iterable<E> it) {
      final Iterator<E> iterator = it.iterator();
      return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
   }

   /**
    * Used to get all but the first element of an {@link Iterable}
    * 
    * @param it
    *        The Iterable
    * @return A {@link Collection} with all elements but the first
    */
   public static <E> Collection<E> tail(final Iterable<E> it) {
      return StreamFactory.stream(it.spliterator()).sequential().skip(1).collect(Collectors.toList());
   }

   /**
    * Used to get the first element of an {@link Iterable}
    *
    * @param it
    *        The Iterable
    * @param defaultValue
    *        A default value to use, if the Iterable is empty
    * @return The first element of it, or the default value
    */
   public static <E> E head(final Iterable<E> it, final E defaultValue) {
      return head(it).orElse(defaultValue);
   }

   /**
    * Used to get the last element of an array
    * 
    * @param elements
    *        An array
    * @return An {@link Optional} containing the last element, or an empty Optional, if elements is empty
    */
   public static <E> Optional<E> last(final E[] elements) {
      return elements.length > 0 ? Optional.of(elements[elements.length - 1]) : Optional.empty();
   }

   /**
    * Used to get the last element of a {@link List}
    * 
    * @param elements
    *        An List
    * @return An {@link Optional} containing the last element, or an empty Optional, if elements is empty
    */
   public static <E> Optional<E> last(final List<E> elements) {
      return elements.isEmpty() ? Optional.empty() : Optional.of(elements.get(elements.size() - 1));
   }

   /**
    * Used to check if a collection contains an element using a specific equals method
    * 
    * @param collection
    *        The collection
    * @param element
    *        The element to check for
    * @param comparator
    *        The comparison method
    * @return An Optional containing the first match, or an empty Optional.
    */
   public static <E1, E2> Optional<E1> firstMatch(final Collection<E1> collection, Equals<E1, E2> comparator, final E2 element) {
      for (E1 e : collection) {
         if (comparator.equal(e, element)) return Optional.ofNullable(e);
      }
      return Optional.empty();
   }

   /**
    * Used to check if two {@link Collection}s contain the same elements. Whereby the comparator is used to check if two elements are equal.
    * 
    * @param c1
    *        The first Collection
    * @param c2
    *        The second Collection
    * @param comparator
    *        The comparator
    * @return {@code true} iff both Collections contain the same elements (according to the comparator)
    */
   public static <E1, E2> boolean haveSameElements(final Collection<E1> c1, final Collection<E2> c2, final Equals<E1, E2> comparator) {
      ArrayList<E1> clone = new ArrayList<>(c1);
      for (E2 e : c2) {
         Optional<E1> match = firstMatch(clone, comparator, e);
         if (match.isPresent()) {
            clone.remove(match.get());
         } else {
            return false;
         }
      }
      return clone.isEmpty();
   }

   /**
    * Used to check if two {@link Collection}s contain the same elements in the same order
    * 
    * @param c1
    *        The first Collection
    * @param c2
    *        The second Collection
    * @return {@code true} iff both Collections contain the same elements in the same order
    */
   public static <E> boolean haveSameElementsInSameOrder(final Collection<E> c1, final Collection<E> c2) {
      return !Functional.zip(c1, c2).anyMatch((pair) -> !AbstractPair.allElementEquals(pair));
   }
}
