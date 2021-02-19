package ch.hsr.ifs.iltis.core.tests.exception;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.exception.ILTISException;


class UnlessTest {

	@Test(expected = ILTISException::class)
	fun throwsIfNull() {
		ILTISException.Unless.notNull("", null);
	}

	@Test
	fun noThrowWhenNotNull() {
		ILTISException.Unless.notNull("", Integer.valueOf(42));
	}

	@Test(expected = ILTISException::class)
	fun throwsIfNotTrue() {
		ILTISException.Unless.isTrue("", false);
	}

	@Test
	fun noThrowWhenTrue() {
		ILTISException.Unless.isTrue("", true);
	}

	@Test(expected = ILTISException::class)
	fun throwsIfNotFalse() {
		ILTISException.Unless.isFalse("", true);
	}

	@Test
	fun noThrowWhenFalse() {
		ILTISException.Unless.isFalse("", false);
	}

	@Test(expected = ILTISException::class)
	fun throwsIfObjOfClassType() {
		val i = 42;
		ILTISException.Unless.notAssignableFrom("", Number::class.java, i);
	}

	@Test
	fun noThrowWhenNotOfClassType() {
		val s = "ILTIS";
		ILTISException.Unless.notInstanceOf("", s, Number::class.java);
	}

	@Test(expected = ILTISException::class)
	fun throwsIfObjNotOfClassType() {
		val s = "ILTIS";
		ILTISException.Unless.instanceOf("", s, Number::class.java);
	}

	@Test
	fun noThrowWhenOfClassType() {
		val i = 42;
		ILTISException.Unless.assignableFrom("", Number::class.java, i);
	}
}
