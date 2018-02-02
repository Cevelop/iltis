package ch.hsr.ifs.iltis.core.tests.collections;

import static ch.hsr.ifs.iltis.core.collections.CollectionUtil.array;
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

import ch.hsr.ifs.iltis.core.collections.CollectionUtil;
import ch.hsr.ifs.iltis.core.exception.ILTISException;


public class CollectionHelperTest {

   @Test
   public void simpleArrayProperlyConstructed() {
      final Integer[] array = CollectionUtil.array(1, 2, 3);
      assertNotNull(array);
      assertEquals(3, array.length);
      assertEquals(Integer.valueOf(1), array[0]);
      assertEquals(Integer.valueOf(2), array[1]);
      assertEquals(Integer.valueOf(3), array[2]);
   }

   @Test
   public void arrayFromListWithElements() {
      final String[] expected = CollectionUtil.array("mockator", "is", "better", "than", "GoogleMock");
      final List<String> list = CollectionUtil.list(expected);
      assertTrue(list instanceof ArrayList<?>);
      assertArrayEquals(expected, list.toArray(new String[list.size()]));
   }

   @Test
   public void emptyList() {
      final List<String> list = CollectionUtil.list();
      assertNotNull(list);
      assertTrue(list.isEmpty());
      assertTrue(list instanceof ArrayList<?>);
   }

   @Test
   public void listFromCollection() {
      final Collection<Integer> numbers = CollectionUtil.list(1, 2, 3);
      final List<Integer> copied = CollectionUtil.list(numbers);
      assertEquals(numbers, copied);
   }

   @Test
   public void emptyUnorderedSet() {
      final Set<String> set = new HashSet<>();
      assertNotNull(set);
      assertTrue(set.isEmpty());
      assertTrue(set instanceof HashSet<?>);
   }

   @Test
   public void emptyOrderPreservingSet() {
      final Set<String> set = new LinkedHashSet<>();
      assertNotNull(set);
      assertTrue(set.isEmpty());
      assertTrue(set instanceof LinkedHashSet<?>);
   }

   @Test
   public void orderPreservingSetWithElementsFromArray() {
      final String[] expected = CollectionUtil.array("mockator", "is", "better", "than", "GoogleMock");
      final Set<String> set = CollectionUtil.orderPreservingSet(expected);
      assertTrue(set instanceof LinkedHashSet<?>);
      assertTrue(CollectionUtil.haveSameElementsInSameOrder(set, Arrays.asList(expected)));
   }

   @Test
   public void sameElementsInSameOrderYieldsTrue() {
      final List<Integer> list = CollectionUtil.list(1, 5, 3, 7, 6, 9);
      final Set<Integer> set = CollectionUtil.orderPreservingSet(1, 5, 3, 7, 6, 9);
      assertTrue(CollectionUtil.haveSameElementsInSameOrder(list, set));
   }

   @Test
   public void sameElementsInDifferentOrderYieldsFalse() {
      final List<Integer> list = CollectionUtil.list(1, 5, 3, 7, 6, 9);
      final Set<Integer> set = CollectionUtil.orderPreservingSet(1, 3, 5, 7, 6, 9);
      assertFalse(CollectionUtil.haveSameElementsInSameOrder(list, set));
   }

   @Test
   public void unorderedSetWithElementsFromArray() {
      final String[] expected = CollectionUtil.array("mockator", "is", "better", "than", "GoogleMock");
      final Set<String> set = CollectionUtil.unorderedSet(expected);
      assertTrue(set instanceof HashSet<?>);
      assertTrue(set.containsAll(Arrays.asList(expected)));
   }

   @Test
   public void emptyOrderPreservingMap() {
      final Map<String, String> map = new LinkedHashMap<>();
      assertNotNull(map);
      assertTrue(map.isEmpty());
      assertTrue(map instanceof LinkedHashMap<?, ?>);
   }

   @Test
   public void emptyUnorderedMap() {
      final Map<String, String> map = new HashMap<>();
      assertNotNull(map);
      assertTrue(map.isEmpty());
      assertTrue(map instanceof HashMap<?, ?>);
   }

   @Test
   public void emptyOrderedMap() {
      final Map<String, String> map = new TreeMap<>();
      assertNotNull(map);
      assertTrue(map.isEmpty());
      assertTrue(map instanceof TreeMap<?, ?>);
   }

   @Test
   public void zipMap() {
      final Map<String, Integer> map = CollectionUtil.zipMap(array("one", "two", "three"), array(1, 2, 3));
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
      CollectionUtil.checkedCast(strings, String.class);
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

      CollectionUtil.checkedCast(objects, String.class);
   }

   @Test
   public void iterableNoNullElements() {
      final List<Integer> numbers = CollectionUtil.list(1, 2, 3, 4, 5);
      assertTrue(CollectionUtil.notNull(numbers));
   }

   @Test
   public void iterableWithOneNullElement() {
      final List<Integer> numbers = CollectionUtil.list(1, 2, null, 4, 5);
      assertFalse(CollectionUtil.notNull(numbers));
   }

   @Test(expected = ILTISException.class)
   public void iterableWithNullListThrowsException() {
      CollectionUtil.notNull((Iterable<Object>) null);
   }

   @Test
   public void isEmptyIsTrueForEmptyList() {
      final List<Integer> empty = CollectionUtil.list();
      assertTrue(CollectionUtil.isEmpty(empty));
   }

   @Test
   public void isEmptyIsFalseForNoneEmptyList() {
      final List<Integer> numbers = CollectionUtil.list(1, 2, 3);
      assertFalse(CollectionUtil.isEmpty(numbers));
   }

   @Test
   public void getTailOfEmptyList() {
      assertEquals(CollectionUtil.list(), CollectionUtil.tail(CollectionUtil.<Integer>list()));
   }

   @Test
   public void getTailOfNonEmptyList() {
      final List<Integer> numbers = CollectionUtil.list(1, 2, 3);
      assertEquals(CollectionUtil.list(2, 3), CollectionUtil.tail(numbers));
   }

   @Test
   public void getHeadOfNonEmptyList() {
      final List<Integer> numbers = CollectionUtil.list(1, 2, 3);
      assertEquals(Integer.valueOf(1), CollectionUtil.head(numbers).get());
   }

   @Test
   public void getHeadOfEmptyListWithDefault() {
      final List<Integer> numbers = CollectionUtil.list();
      assertEquals(Integer.valueOf(1), CollectionUtil.head(numbers, 1));
   }

   @Test
   public void getHeadOfEmptyList() {
      final List<Integer> numbers = CollectionUtil.list();
      assertTrue(!CollectionUtil.head(numbers).isPresent());
   }

   @Test
   public void getLastOfNonEmptyList() {
      final List<Integer> numbers = CollectionUtil.list(1, 2, 3);
      assertEquals(Integer.valueOf(3), CollectionUtil.last(numbers).get());
   }

   @Test
   public void getLastOfEmptyList() {
      final List<Integer> numbers = CollectionUtil.list();
      assertTrue(!CollectionUtil.last(numbers).isPresent());
   }
}
