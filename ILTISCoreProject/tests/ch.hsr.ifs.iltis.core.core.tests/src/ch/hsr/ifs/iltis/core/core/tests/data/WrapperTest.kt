package ch.hsr.ifs.iltis.core.core.tests.data;

import org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.core.data.Wrapper;


class WrapperTest {

	@Test
	fun lambdaEscape() {
		val w = Wrapper("")

		val strings = arrayOf("foo", "bar", "baz", "foobar", "foobaz")
		Arrays.stream(strings).forEach {
			if (it.contains("foo")) {
				w.wrapped += it;
			}
		}

		assertEquals("foofoobarfoobaz", w.wrapped);
	}

}
