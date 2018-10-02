package ch.hsr.ifs.iltis.core.core.tests.functional

import org.junit.Test
import ch.hsr.ifs.iltis.core.core.functional.Functional
import ch.hsr.ifs.iltis.core.core.functional.StreamUtil
import org.junit.Assert.assertEquals


class StreamUtilTest {

	@Test
	fun `toMap() Collector sets missing key to 'null'`() {
		val keys = arrayOf("foo", "bar", "baz")
		val vals = arrayOf(1, 2, 3, 4)

		val actual = Functional.zip(keys, vals).collect(StreamUtil.toMap())

		val expected = mapOf(keys[0] to vals[0], keys[1] to vals[1], keys[2] to vals[2], null to vals[3])

		assertEquals(expected, actual);
	}

}
