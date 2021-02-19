package ch.hsr.ifs.iltis.core.tests.functional

import java.util.Optional

import org.junit.Assert.assertEquals
import org.junit.Test

import ch.hsr.ifs.iltis.core.data.Wrapper
import ch.hsr.ifs.iltis.core.functional.OptionalUtil
import org.junit.Rule
import org.junit.rules.ExpectedException


class OptionalUtilTest {

	@Test
	fun `ifPresent() is called then value of Optional is present`() {
		val source = Optional.of("foo");
		val value = Wrapper("old");
		OptionalUtil.of(source).ifPresent { value.wrapped = it + "bar" }.ifNotPresent(Runnable { value.wrapped += "baz" })

		assertEquals("foobar", value.wrapped);
	}

	@Test
	fun `ifNotPresent() is called then value of Optional is not present`() {
		val source = Optional.empty<String>()
		val value = Wrapper("foo")
		OptionalUtil.of(source).ifPresent { value.wrapped = it + "bar" }.ifNotPresent(Runnable { value.wrapped += "baz" })
		assertEquals("foobaz", value.wrapped)
	}

	@Test(expected = Exception::class)
	fun `ifPresentT() throws if Optional is set`() {
		val source = Optional.of("foo")
		OptionalUtil.of(source).ifPresentT<Exception> {
			throw Exception()
		}
	}

	@Test(expected = Exception::class)
	fun `ifNotPresentT() throws if Optional is not set`() {
		val source = Optional.empty<String>();
		val value = Wrapper("old");
		OptionalUtil.of(source).ifNotPresentT<Exception> {
			throw Exception()
		}
		assertEquals("old", value.wrapped);
	}

}
