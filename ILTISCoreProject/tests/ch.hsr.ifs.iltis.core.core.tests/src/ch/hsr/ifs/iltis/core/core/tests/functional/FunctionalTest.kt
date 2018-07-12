package ch.hsr.ifs.iltis.core.core.tests.functional;

import ch.hsr.ifs.iltis.core.core.collections.CollectionUtil.array;
import ch.hsr.ifs.iltis.core.core.collections.CollectionUtil.list;
import ch.hsr.ifs.iltis.core.core.functional.StreamFactory.stream;
import org.junit.Assert.assertArrayEquals;
import org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert
import org.junit.Test;

import ch.hsr.ifs.iltis.core.core.collections.CollectionUtil;
import ch.hsr.ifs.iltis.core.core.data.AbstractPair;
import ch.hsr.ifs.iltis.core.core.functional.Functional;
import ch.hsr.ifs.iltis.core.core.functional.StreamPair;
import ch.hsr.ifs.iltis.core.core.functional.StreamTriple;
import ch.hsr.ifs.iltis.core.core.functional.functions.Equals;


internal fun assertEquals(l: AbstractPair<*, *>, r:  AbstractPair<*, *>) {
   val comparator = object : AbstractPair.AbstractPairEquals<AbstractPair<*, *>>() {

      override fun equal(l: AbstractPair<*, *>, r: AbstractPair<*, *>): Boolean {
         var fsteq: Boolean
         var sndeq: Boolean
         if (accessFirst(l) is AbstractPair<*, *> && accessFirst(r) is AbstractPair<*, *>) {
            fsteq = equal(accessFirst(l) as AbstractPair<*, *>, accessFirst(r) as AbstractPair<*, *>);
         } else if (accessFirst(l) == null && accessFirst(r) == null) {
            fsteq = true;
         } else {
            fsteq = accessFirst(l).equals(accessFirst(r));
         }
         if (accessSecond(l) is AbstractPair<*, *> && accessSecond(r) is AbstractPair<*, *>) {
            sndeq = equal(accessSecond(l)  as AbstractPair<*, *>, accessSecond(r)  as AbstractPair<*, *>);
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

internal fun <P1 : AbstractPair<*, *>, P2 : AbstractPair<*, *>> assertEquals(l: List<P1>, r: List<P2>) {
   for(i in 0 until l.size) {
      assertEquals(l.get(i), r.get(i));
   }
}



class FunctionalTest {

   internal class TestPair<F, S>(first: F, second: S) : AbstractPair<F, S>(first, second) {

      fun first() = first

      fun second() = second

   }

   internal class TestTriple<F, S, T>(first: F, second: S, third: T) : AbstractPair<F, TestPair<S, T>>(first ,TestPair(second, third)) {

      fun first() = first

      fun second() = second.first()

      fun third() = second.second()

   }

   @Test
   fun `as() preserves type identity`() {
      val m = HashMap<String, String>()
      val hm = Functional.`as`<HashMap<String, String>>(m)
      assertTrue(hm != null)
   }

   @Test
   fun `zipWithDefaults() calls supplied function to generate default values for missing elements`() {
      val xs = stream(1, 2, 3)
      val ys = stream("foo", "bar")

      val actual = Functional.zipWithDefaults(xs, ys, {y -> y.toInt()}, {x -> x.toString()}).collect(
            Collectors.toList())

      val expected = CollectionUtil.list(TestPair(1, "foo"), TestPair(2, "bar"), TestPair(3, "3"))
      assertEquals(expected, actual);
   }

   @Test
   fun `zip() works with Collections`() {
      val xs = CollectionUtil.list(1, 2)
      val ys = CollectionUtil.list("foo", "bar", "7")

      val actual = Functional.zip(xs, ys).collect(Collectors.toList())

      val expected = CollectionUtil.list(TestPair(1, "foo"), TestPair(2, "bar"), TestPair(null, "7"))
      assertEquals(expected, actual)
   }

   @Test
   fun `zip() works with Arrays`() {
      val xs = arrayOf(1, 2, 3 )
      val ys = arrayOf("foo", "bar")

      val actual = Functional.zip(xs, ys).collect(Collectors.toList())

      val expected = CollectionUtil.list(TestPair(1, "foo"), TestPair(2, "bar"), TestPair(3, null))
      assertEquals(expected, actual);
   }

   @Test
   fun `zip() on a Map zips its keys with its values`() {
      val xs = CollectionUtil.map(array(1, 2, 3), array("foo", "bar"));

      val actual = Functional.zip(xs).collect(Collectors.toList());

      val expected = CollectionUtil.list(TestPair(1, "foo"), TestPair(2, "bar"), TestPair(3, null))
      assertEquals(expected, actual)
   }

   @Test
   fun `zip() works on Triples`() {
      val xs = stream(1, 2, 3);
      val ys = stream("foo", "bar");
      val zs = stream(true, false);

      val actual = Functional.zipWithDefaults(xs, ys, zs, {b, _ -> b.toInt()}, {a, _ -> a.toString()}, {a, _ -> a > 2}).collect(Collectors.toList());

      val expected = CollectionUtil.list(TestTriple(1, "foo", true), TestTriple(2, "bar", false), TestTriple(3, "3", true));
      assertEquals(expected, actual);
   }

   @Test
   fun `zip() works on Collections of Triples`() {
      val xs = list(1, 2, 3)
      val ys = list("foo", "bar")
      val zs = list(true, false)

      val actual = Functional.zip(xs, ys, zs).collect(Collectors.toList())

      val expected = CollectionUtil.list(TestTriple(1, "foo", true), TestTriple(2, "bar", false), TestTriple(3, null, null))
      assertEquals(expected, actual)
   }

   @Test
   fun `zip() works on Arrays of Triples`() {
      val xs = array(1, 2, 3)
      val ys = array("foo", "bar")
      val zs = array(true, false)

      val actual = Functional.zip(xs, ys, zs).collect(Collectors.toList())

      val expected = CollectionUtil.list(TestTriple(1, "foo", true), TestTriple(2, "bar", false), TestTriple(3, null, null))
      assertEquals(expected, actual)
   }

   @Test
   fun `map() applys the given function to all elements of the given Array`() {
      val xs = array(1, 2, 3, 4, 5)
      val expected = array(false, false, true, true, true)

      val actual = Functional.map(xs, {it > 2}, {arrayOfNulls(it)});

      assertArrayEquals(expected, actual);
   }

   @Test
   fun `asOrNull returns the given value iff the runtime types match, but 'null' otherwise`() {
      val source = "foo" as Any;
      val actual = Functional.asOrNull(String::class.java, source);
      Assert.assertEquals("foo", actual);

      val actualInt = Functional.asOrNull(Integer::class.java, source);
      Assert.assertNull(actualInt);
   }

   @Test
   fun `as returns the given value off the runtime types match`() {
      val source = "foo" as Any;
      val actual = Functional.`as`<String>(source);
      Assert.assertEquals("foo", actual);
   }

   @Test(expected = ClassCastException::class)
   fun `as throws if the the runtime types don't match`() {
      val source = "foo" as Any;
      val actual = Functional.`as`<Int>(source);
      Assert.assertEquals("foo", actual);
   }

}
