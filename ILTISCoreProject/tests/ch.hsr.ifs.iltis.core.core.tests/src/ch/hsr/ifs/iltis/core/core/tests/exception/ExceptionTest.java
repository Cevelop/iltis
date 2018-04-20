package ch.hsr.ifs.iltis.core.core.tests.exception;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;


public class ExceptionTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Test
   public void preservesExceptionMessage() {
      thrown.expect(ILTISException.class);
      thrown.expectMessage("Invalid XYZ");
      throw new ILTISException("Invalid XYZ").rethrowUnchecked();
   }

   @Test
   public void rethrowNestedExceptionWorks() throws Exception {
      thrown.expect(IllegalArgumentException.class);
      thrown.expectMessage("Number not in range");

      try {
         throw new ILTISException(new IllegalArgumentException("Number not in range"));
      }
      catch (final ILTISException e1) {
         e1.rethrow();
      }
   }
}
