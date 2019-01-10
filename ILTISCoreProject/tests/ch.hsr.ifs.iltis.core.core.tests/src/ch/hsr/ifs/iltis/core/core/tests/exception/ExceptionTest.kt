package ch.hsr.ifs.iltis.core.core.tests.exception;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;


class ExceptionTest {

	@Rule
	@JvmField
	val thrown = ExpectedException.none();

	@Test
	fun preservesExceptionMessage() {
		thrown.expect(ILTISException::class.java);
		thrown.expectMessage("Invalid XYZ");
		throw ILTISException("Invalid XYZ").rethrowUnchecked();
	}

	@Test
	fun rethrowNestedExceptionWorks() {
		thrown.expect(IllegalArgumentException::class.java);
		thrown.expectMessage("Number not in range");

		try {
			throw ILTISException(IllegalArgumentException("Number not in range"));
		} catch (e1: ILTISException) {
			e1.rethrow();
		}
	}
}
