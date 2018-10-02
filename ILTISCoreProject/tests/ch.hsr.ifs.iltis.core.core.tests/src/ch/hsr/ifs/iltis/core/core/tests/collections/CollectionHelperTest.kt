package ch.hsr.ifs.iltis.core.core.tests.collections

import ch.hsr.ifs.iltis.core.core.collections.CollectionUtil.array
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.TreeMap

import org.junit.Test

import ch.hsr.ifs.iltis.core.core.collections.CollectionUtil
import ch.hsr.ifs.iltis.core.core.exception.ILTISException


class CollectionHelperTest {

	@Test
	fun simpleArrayProperlyConstructed() {
		val array = CollectionUtil.array(1, 2, 3)
		assertNotNull(array)
		assertEquals(3, array.size)
		assertEquals(Integer.valueOf(1), array[0])
		assertEquals(Integer.valueOf(2), array[1])
		assertEquals(Integer.valueOf(3), array[2])
	}

	@Test
	fun arrayFromListWithElements() {
		val expected = CollectionUtil.array("mockator", "is", "better", "than", "GoogleMock")
		val list = CollectionUtil.list(*expected)
		assertTrue(list is ArrayList<*>)
		assertArrayEquals(expected, (list as ArrayList<*>).toArray(arrayOfNulls(list.size)))
	}

	@Test
	fun emptyList() {
		val list = CollectionUtil.list<String>()
		assertNotNull(list)
		assertTrue(list.isEmpty())
		assertTrue(list is ArrayList<*>)
	}

	@Test
	fun listFromCollection() {
		val numbers = CollectionUtil.list(1, 2, 3)
		val copied = CollectionUtil.list(numbers)
		assertEquals(numbers, copied)
	}

	@Test
	fun orderPreservingSetWithElementsFromArray() {
		val expected = CollectionUtil.array("mockator", "is", "better", "than", "GoogleMock")
		val set = CollectionUtil.orderPreservingSet(expected)
		assertTrue(set is LinkedHashSet<*>)
		assertTrue(CollectionUtil.haveSameElementsInSameOrder(set, Arrays.asList(expected)))
	}

	@Test
	fun sameElementsInSameOrderYieldsTrue() {
		val list = CollectionUtil.list(1, 5, 3, 7, 6, 9)
		val set = CollectionUtil.orderPreservingSet(1, 5, 3, 7, 6, 9)
		assertTrue(CollectionUtil.haveSameElementsInSameOrder(list, set))
	}

	@Test
	fun sameElementsInDifferentOrderYieldsFalse() {
		val list = CollectionUtil.list(1, 5, 3, 7, 6, 9)
		val set = CollectionUtil.orderPreservingSet(1, 3, 5, 7, 6, 9)
		assertFalse(CollectionUtil.haveSameElementsInSameOrder(list, set))
	}

	@Test
	fun unorderedSetWithElementsFromArray() {
		val expected = CollectionUtil.array("mockator", "is", "better", "than", "GoogleMock")
		val set = CollectionUtil.unorderedSet(expected)
		assertTrue(set is HashSet<*>)
		assertTrue(set.containsAll(Arrays.asList(expected)))
	}

	@Test
	fun zipMap() {
		val map = CollectionUtil.zipMap(array("one", "two", "three"), array(1, 2, 3))
		assertEquals(map.get("one"), Integer.valueOf(1))
		assertEquals(map.get("two"), Integer.valueOf(2))
		assertEquals(map.get("three"), Integer.valueOf(3))
	}

	@Test
	fun checkedCastWithTypeCompatibleObjects() {
		val strings = object : ArrayList<Any>() {
			init {
				add("one")
				add("two")
				add("three")
			}
		}
		CollectionUtil.checkedCast(strings, String::class.java)
	}

	@Test(expected = ClassCastException::class)
	fun checkedCastWithUncompatibleObjects() {
		val objects = object : ArrayList<Any>() {

			init {
				add("one")
				add(2)
				add("three")
			}
		}

		CollectionUtil.checkedCast(objects, String::class.java)
	}

	@Test
	fun iterableNoNullElements() {
		assertTrue(CollectionUtil.notNull(CollectionUtil.list(1, 2, 3, 4, 5)))
	}

	@Test
	fun iterableWithOneNullElement() {
		assertFalse(CollectionUtil.notNull(CollectionUtil.list(1, 2, null, 4, 5)))
	}

	@Test(expected = ILTISException::class)
	fun iterableWithNullListThrowsException() {
		CollectionUtil.notNull(null as Iterable<Any>?)
	}

	@Test
	fun isEmptyIsTrueForEmptyList() {
		assertTrue(CollectionUtil.isEmpty(CollectionUtil.list<Any>()))
	}

	@Test
	fun isEmptyIsFalseForNoneEmptyList() {
		assertFalse(CollectionUtil.isEmpty(CollectionUtil.list(1, 2, 3)))
	}

	@Test
	fun getTailOfEmptyList() {
		assertEquals(CollectionUtil.list<Int>(), CollectionUtil.tail(CollectionUtil.list<Int>()))
	}

	@Test
	fun getTailOfNonEmptyList() {
		assertEquals(CollectionUtil.list(2, 3), CollectionUtil.tail(CollectionUtil.list(1, 2, 3)))
	}

	@Test
	fun getHeadOfNonEmptyList() {
		assertEquals(Integer.valueOf(1), CollectionUtil.head(CollectionUtil.list(1, 2, 3)).get())
	}

	@Test
	fun getHeadOfEmptyListWithDefault() {
		assertEquals(1, CollectionUtil.head(CollectionUtil.list(), 1))
	}

	@Test
	fun getHeadOfEmptyList() {
		assertTrue(!CollectionUtil.head(CollectionUtil.list<Any>()).isPresent())
	}

	@Test
	fun getLastOfNonEmptyList() {
		assertEquals(Integer.valueOf(3), CollectionUtil.last(CollectionUtil.list(1, 2, 3)).get())
	}

	@Test
	fun getLastOfEmptyList() {
		assertTrue(!CollectionUtil.last(CollectionUtil.list<Int>()).isPresent())
	}
}
