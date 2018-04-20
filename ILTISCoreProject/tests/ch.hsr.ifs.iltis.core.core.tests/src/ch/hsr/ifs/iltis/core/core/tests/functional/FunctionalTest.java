package ch.hsr.ifs.iltis.core.core.tests.functional;

import static ch.hsr.ifs.iltis.core.core.collections.CollectionUtil.array;
import static ch.hsr.ifs.iltis.core.core.collections.CollectionUtil.list;
import static ch.hsr.ifs.iltis.core.core.functional.StreamFactory.stream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import ch.hsr.ifs.iltis.core.core.collections.CollectionUtil;
import ch.hsr.ifs.iltis.core.core.data.AbstractPair;
import ch.hsr.ifs.iltis.core.core.functional.Functional;
import ch.hsr.ifs.iltis.core.core.functional.StreamPair;
import ch.hsr.ifs.iltis.core.core.functional.StreamTriple;
import ch.hsr.ifs.iltis.core.core.functional.functions.Equals;


public class FunctionalTest {

   @Test
   public void unsecureCastYieldsCastedValue() {
      final Map<String, String> m = new HashMap<>();
      final HashMap<String, String> hm = Functional.as(m);
      assertTrue(hm != null);
   }

   @Test
   public void zipWithDefaults() {
      Stream<Integer> as = stream(1, 2, 3);
      Stream<String> bs = stream("foo", "bar");

      List<StreamPair<Integer, String>> actual = Functional.zipWithDefaults(as, bs, (b) -> Integer.valueOf(b), (a) -> String.valueOf(a)).collect(
            Collectors.toList());

      List<TestPair<Integer, String>> expected = CollectionUtil.list(new TestPair<>(1, "foo"), new TestPair<>(2, "bar"), new TestPair<>(3, "3"));
      assertEquals(expected, actual);
   }

   @Test
   public void zipCollections() {
      List<Integer> as = CollectionUtil.list(1, 2);
      List<String> bs = CollectionUtil.list("foo", "bar", "7");

      List<StreamPair<Integer, String>> actual = Functional.zip(as, bs).collect(Collectors.toList());

      List<TestPair<Integer, String>> expected = CollectionUtil.list(new TestPair<>(1, "foo"), new TestPair<>(2, "bar"), new TestPair<>(null, "7"));
      assertEquals(expected, actual);
   }

   @Test
   public void zipArrays() {
      Integer[] as = new Integer[] { 1, 2, 3 };
      String[] bs = new String[] { "foo", "bar" };

      List<StreamPair<Integer, String>> actual = Functional.zip(as, bs).collect(Collectors.toList());

      List<TestPair<Integer, String>> expected = CollectionUtil.list(new TestPair<>(1, "foo"), new TestPair<>(2, "bar"), new TestPair<>(3, null));
      assertEquals(expected, actual);
   }

   @Test
   public void zipMap() {
      Map<Integer, String> as = CollectionUtil.map(array(1, 2, 3), array("foo", "bar"));

      List<StreamPair<Integer, String>> actual = Functional.zip(as).collect(Collectors.toList());

      List<TestPair<Integer, String>> expected = CollectionUtil.list(new TestPair<>(1, "foo"), new TestPair<>(2, "bar"), new TestPair<>(3, null));
      assertEquals(expected, actual);
   }

   @Test
   public void zipTriple() {
      Stream<Integer> as = stream(1, 2, 3);
      Stream<String> bs = stream("foo", "bar");
      Stream<Boolean> cs = stream(true, false);

      List<StreamTriple<Integer, String, Boolean>> actual = Functional.zipWithDefaults(as, bs, cs, (b, c) -> Integer.valueOf(b), (a, c) -> String
            .valueOf(a), (a, b) -> a > 2).collect(Collectors.toList());

      List<TestTriple<Integer, String, Boolean>> expected = CollectionUtil.list(new TestTriple<>(1, "foo", true), new TestTriple<>(2, "bar", false),
            new TestTriple<>(3, "3", true));
      assertEquals(expected, actual);
   }

   @Test
   public void zipTripleCollection() {
      List<Integer> as = list(1, 2, 3);
      List<String> bs = list("foo", "bar");
      List<Boolean> cs = list(true, false);

      List<StreamTriple<Integer, String, Boolean>> actual = Functional.zip(as, bs, cs).collect(Collectors.toList());

      List<TestTriple<Integer, String, Boolean>> expected = CollectionUtil.list(new TestTriple<>(1, "foo", true), new TestTriple<>(2, "bar", false),
            new TestTriple<>(3, null, null));
      assertEquals(expected, actual);
   }

   @Test
   public void zipTripleArray() {
      Integer[] as = array(1, 2, 3);
      String[] bs = array("foo", "bar");
      Boolean[] cs = array(true, false);

      List<StreamTriple<Integer, String, Boolean>> actual = Functional.zip(as, bs, cs).collect(Collectors.toList());

      List<TestTriple<Integer, String, Boolean>> expected = CollectionUtil.list(new TestTriple<>(1, "foo", true), new TestTriple<>(2, "bar", false),
            new TestTriple<>(3, null, null));
      assertEquals(expected, actual);
   }

   @Test
   public void map() {
      Integer[] as = array(1, 2, 3, 4, 5);
      Boolean[] expected = array(false, false, true, true, true);

      Boolean[] actual = Functional.map(as, (it) -> it > 2, Boolean[]::new);

      assertArrayEquals(expected, actual);
   }

   @Test
   public void asOrNull() {
      Object source = "foo";
      String actual = Functional.asOrNull(String.class, source);
      Assert.assertEquals("foo", actual);

      Integer actualInt = Functional.asOrNull(Integer.class, source);
      Assert.assertNull(actualInt);
   }

   @Test
   public void as() {
      Object source = "foo";
      String actual = Functional.as(source);
      Assert.assertEquals("foo", actual);
   }

   @Test(expected = ClassCastException.class)
   public void asExc() {
      Object source = "foo";
      Integer actual = Functional.as(source);
      Assert.assertEquals("foo", actual);
   }

   protected class TestPair<F, S> extends AbstractPair<F, S> {

      public TestPair(F first, S second) {
         super(first, second);
      }

      public F first() {
         return first;
      }

      public S second() {
         return second;
      }

   }

   protected class TestTriple<F, S, T> extends AbstractPair<F, TestPair<S, T>> {

      public TestTriple(F first, S second, T third) {
         super(first, new TestPair<S, T>(second, third));
      }

      public F first() {
         return first;
      }

      public S second() {
         return second.first();
      }

      public T third() {
         return second.second();
      }

   }

   protected static <P1 extends AbstractPair<?, ?>, P2 extends AbstractPair<?, ?>> void assertEquals(List<P1> l, List<P2> r) {
      for (int i = 0; i < l.size(); i++) {
         assertEquals(l.get(i), r.get(i));
      }
   }

   protected static void assertEquals(AbstractPair<?, ?> l, AbstractPair<?, ?> r) {
      Equals<AbstractPair<?, ?>, AbstractPair<?, ?>> comparator = new AbstractPair.AbstractPairEquals<AbstractPair<?, ?>>() {

         @Override
         public boolean equal(AbstractPair<?, ?> l, AbstractPair<?, ?> r) {
            boolean fsteq = false;
            boolean sndeq = false;
            if (accessFirst(l) instanceof AbstractPair && accessFirst(r) instanceof AbstractPair) {
               fsteq = equal((AbstractPair<?, ?>) accessFirst(l), (AbstractPair<?, ?>) accessFirst(r));
            } else if (accessFirst(l) == null && accessFirst(r) == null) {
               fsteq = true;
            } else {
               fsteq = accessFirst(l).equals(accessFirst(r));
            }
            if (accessSecond(l) instanceof AbstractPair && accessSecond(r) instanceof AbstractPair) {
               sndeq = equal((AbstractPair<?, ?>) accessSecond(l), (AbstractPair<?, ?>) accessSecond(r));
            } else if (accessSecond(l) == null && accessSecond(r) == null) {
               sndeq = true;
            } else {
               sndeq = accessSecond(l).equals(accessSecond(r));
            }
            return fsteq && sndeq;
         }

      };
      assertTrue(String.format("Pair were not equal: %s, %s", l, r), l.equals(r, comparator));
   }
}
