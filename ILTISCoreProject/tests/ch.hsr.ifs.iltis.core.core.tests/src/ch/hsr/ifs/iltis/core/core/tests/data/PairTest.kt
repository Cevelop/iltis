package ch.hsr.ifs.iltis.core.core.tests.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

import java.util.HashMap

import org.junit.Test

import ch.hsr.ifs.iltis.core.core.data.AbstractPair


public class PairTest {

   private class Pair<T1, T2>(first: T1, second: T2) : AbstractPair<T1, T2>(first, second) {

      fun first() = first

      fun second() = second

   }

	
   @Test
   fun `Pairs with equal content compare equal`() {
      val marriage = mapOf(
         Pair("Ursus", "Nadeschkin") to 12,
         Pair("Bill", "Hillary") to 15,
         Pair("Bill", "Melinda") to 22
      )

      val numberOfYears = marriage[Pair("Bill", "Hillary")]
      assertEquals(15, numberOfYears)
      assertNull(marriage[Pair("Charles", "Camilla")])
   }

   @Test
   fun `toString() on a pair returns '(fst, snd)'`() {
      val couple = Pair("Bill", "Hillary")
      val s = couple.toString()
      assertEquals("(Bill, Hillary)", s)
   }

   @Test
   fun `the elements of a Pair can be accessed via 'first' and 'last'`() {
      val t = Pair("QuestionOfLive", 42)
      val s = t.first()
      val i = t.second()
      assertEquals("QuestionOfLive", s)
      assertEquals(42, i)
   }

   @Test
   fun `toString() on nested Pairs performs expansion of the inner Pairs`() {
      val marriage = Pair("Bill", Pair("Hillary", 15))
      val s = marriage.toString()
      assertEquals("(Bill, Hillary, 15)", s)
   }

   @Test
   fun allElementFirstNestedEquals() {
      var foo = Pair(Pair("Cigar", "Clinton"), "William")
      assertFalse(AbstractPair.allElementEquals(foo))
      foo = Pair(Pair("Me!", "Me!"), "Me!")
      assertTrue(AbstractPair.allElementEquals(foo))
   }

   @Test
   fun allElementSecondNestedEquals() {
      var foo = Pair("William", Pair("Cigar", "Clinton"))
      assertFalse(AbstractPair.allElementEquals(foo))
      foo = Pair("LOL", Pair("LOL", "LOL"))
      assertTrue(AbstractPair.allElementEquals(foo))
   }

   @Test
   fun allElementBothNested() {
      var foo = Pair(Pair("Cigar", "Cigar"), Pair("Clinton", "Clinton"))
      assertFalse(AbstractPair.allElementEquals(foo))
      foo = Pair(Pair("Cigar", "Cigar"), Pair("Cigar", "Cigar"))
      assertTrue(AbstractPair.allElementEquals(foo))
   }

   @Test
   fun allElementNull() {
      var foo = Pair(Pair<String?, String?>("Cigar", "Cigar"), Pair<String?, String?>(null, null))
      assertFalse(AbstractPair.allElementEquals(foo))
      foo = Pair(Pair<String?, String?>(null, null), Pair<String?, String?>(null, null))
      assertTrue(AbstractPair.allElementEquals(foo))
   }

   @Test
   fun allElementEquals() {
      var foo = Pair("William", "Clinton")
      assertFalse(AbstractPair.allElementEquals(foo))
      foo = Pair("William", "William")
      assertTrue(AbstractPair.allElementEquals(foo))
   }

}
