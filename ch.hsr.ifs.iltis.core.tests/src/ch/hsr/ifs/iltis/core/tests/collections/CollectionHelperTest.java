package ch.hsr.ifs.iltis.core.tests.collections;

import static ch.hsr.ifs.iltis.core.collections.CollectionHelper.array;
import static ch.hsr.ifs.iltis.core.collections.CollectionHelper.orderPreservingMap;
import static ch.hsr.ifs.iltis.core.collections.CollectionHelper.orderedMap;
import static ch.hsr.ifs.iltis.core.collections.CollectionHelper.unorderedMap;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.collections.CollectionHelper;
import ch.hsr.ifs.iltis.core.exception.ILTISException;


public class CollectionHelperTest {

   @Test
   public void simpleArrayProperlyConstructed() {
      final Integer[] array = CollectionHelper.array(1, 2, 3);
      assertNotNull(array);
      assertEquals(3, array.length);
      assertEquals(Integer.valueOf(1), array[0]);
      assertEquals(Integer.valueOf(2), array[1]);
      assertEquals(Integer.valueOf(3), array[2]);
   }

   @Test
   public void arrayFromListWithElements() {
      final String[] expected = CollectionHelper.array("mockator", "is", "better", "than", "GoogleMock");
      final List<String> list = CollectionHelper.list(expected);
      assertTrue(list instanceof ArrayList<?>);
      assertArrayEquals(expected, list.toArray(new String[list.size()]));
   }

   @Test
   public void emptyList() {
      final List<String> list = CollectionHelper.list();
      assertNotNull(list);
      assertTrue(list.isEmpty());
      assertTrue(list instanceof ArrayList<?>);
   }

   @Test
   public void listFromCollection() {
      final Collection<Integer> numbers = CollectionHelper.list(1, 2, 3);
      final List<Integer> copied = CollectionHelper.list(numbers);
      assertEquals(numbers, copied);
   }

   @Test
   public void emptyUnorderedSet() {
      final Set<String> set = CollectionHelper.unorderedSet();
      assertNotNull(set);
      assertTrue(set.isEmpty());
      assertTrue(set instanceof HashSet<?>);
   }

   @Test
   public void emptyOrderPreservingSet() {
      final Set<String> set = CollectionHelper.orderPreservingSet();
      assertNotNull(set);
      assertTrue(set.isEmpty());
      assertTrue(set instanceof LinkedHashSet<?>);
   }

   @Test
   public void orderPreservingSetWithElementsFromArray() {
      final String[] expected = CollectionHelper.array("mockator", "is", "better", "than", "GoogleMock");
      final Set<String> set = CollectionHelper.orderPreservingSet(expected);
      assertTrue(set instanceof LinkedHashSet<?>);
      assertTrue(CollectionHelper.haveSameElementsInSameOrder(set, Arrays.asList(expected)));
   }

   @Test
   public void sameElementsInSameOrderYieldsTrue() {
      final List<Integer> list = CollectionHelper.list(1, 5, 3, 7, 6, 9);
      final Set<Integer> set = CollectionHelper.orderPreservingSet(1, 5, 3, 7, 6, 9);
      assertTrue(CollectionHelper.haveSameElementsInSameOrder(list, set));
   }

   @Test
   public void sameElementsInDifferentOrderYieldsFalse() {
      final List<Integer> list = CollectionHelper.list(1, 5, 3, 7, 6, 9);
      final Set<Integer> set = CollectionHelper.orderPreservingSet(1, 3, 5, 7, 6, 9);
      assertFalse(CollectionHelper.haveSameElementsInSameOrder(list, set));
   }

   @Test
   public void unorderedSetWithElementsFromArray() {
      final String[] expected = CollectionHelper.array("mockator", "is", "better", "than", "GoogleMock");
      final Set<String> set = CollectionHelper.unorderedSet(expected);
      assertTrue(set instanceof HashSet<?>);
      assertTrue(set.containsAll(Arrays.asList(expected)));
   }

   @Test
   public void emptyOrderPreservingMap() {
      final Map<String, String> map = orderPreservingMap();
      assertNotNull(map);
      assertTrue(map.isEmpty());
      assertTrue(map instanceof LinkedHashMap<?, ?>);
   }

   @Test
   public void emptyUnorderedMap() {
      final Map<String, String> map = unorderedMap();
      assertNotNull(map);
      assertTrue(map.isEmpty());
      assertTrue(map instanceof HashMap<?, ?>);
   }

   @Test
   public void emptyOrderedMap() {
      final Map<String, String> map = orderedMap();
      assertNotNull(map);
      assertTrue(map.isEmpty());
      assertTrue(map instanceof TreeMap<?, ?>);
   }

   @Test
   public void zipMap() {
      final Map<String, Integer> map = CollectionHelper.zipMap(array("one", "two", "three"), array(1, 2, 3));
      assertEquals(map.get("one"), Integer.valueOf(1));
      assertEquals(map.get("two"), Integer.valueOf(2));
      assertEquals(map.get("three"), Integer.valueOf(3));
   }

   @Test
   public void checkedCastWithTypeCompatibleObjects() {
      final List<Object> strings = new ArrayList<Object>() {

         /**
          *
          */
         private static final long serialVersionUID = 5534740326174887503L;

         {
            add("one");
            add("two");
            add("three");
         }
      };
      CollectionHelper.checkedCast(strings, String.class);
   }

   @Test(expected = ClassCastException.class)
   public void checkedCastWithUncompatibleObjects() {
      final List<Object> objects = new ArrayList<Object>() {

         /**
          *
          */
         private static final long serialVersionUID = -3125602429432402437L;

         {
            add("one");
            add(2);
            add("three");
         }
      };

      CollectionHelper.checkedCast(objects, String.class);
   }

   @Test
   public void iterableNoNullElements() {
      final List<Integer> numbers = CollectionHelper.list(1, 2, 3, 4, 5);
      assertTrue(CollectionHelper.notNull(numbers));
   }

   @Test
   public void iterableWithOneNullElement() {
      final List<Integer> numbers = CollectionHelper.list(1, 2, null, 4, 5);
      assertFalse(CollectionHelper.notNull(numbers));
   }

   @Test(expected = ILTISException.class)
   public void iterableWithNullListThrowsException() {
      CollectionHelper.notNull((Iterable<Object>) null);
   }

   @Test
   public void isEmptyIsTrueForEmptyList() {
      final List<Integer> empty = CollectionHelper.list();
      assertTrue(CollectionHelper.isEmpty(empty));
   }

   @Test
   public void isEmptyIsFalseForNoneEmptyList() {
      final List<Integer> numbers = CollectionHelper.list(1, 2, 3);
      assertFalse(CollectionHelper.isEmpty(numbers));
   }

   @Test
   public void getTailOfEmptyList() {
      assertEquals(CollectionHelper.list(), CollectionHelper.tail(CollectionHelper.<Integer>list()));
   }

   @Test
   public void getTailOfNonEmptyList() {
      final List<Integer> numbers = CollectionHelper.list(1, 2, 3);
      assertEquals(CollectionHelper.list(2, 3), CollectionHelper.tail(numbers));
   }

   @Test
   public void getHeadOfNonEmptyList() {
      final List<Integer> numbers = CollectionHelper.list(1, 2, 3);
      assertEquals(Integer.valueOf(1), CollectionHelper.head(numbers).get());
   }

   @Test
   public void getHeadOfEmptyListWithDefault() {
      final List<Integer> numbers = CollectionHelper.list();
      assertEquals(Integer.valueOf(1), CollectionHelper.head(numbers, 1));
   }

   @Test
   public void getHeadOfEmptyList() {
      final List<Integer> numbers = CollectionHelper.list();
      assertTrue(!CollectionHelper.head(numbers).isPresent());
   }

   @Test
   public void getLastOfNonEmptyList() {
      final List<Integer> numbers = CollectionHelper.list(1, 2, 3);
      assertEquals(Integer.valueOf(3), CollectionHelper.last(numbers).get());
   }

   @Test
   public void getLastOfEmptyList() {
      final List<Integer> numbers = CollectionHelper.list();
      assertTrue(!CollectionHelper.last(numbers).isPresent());
   }
}
