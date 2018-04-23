package ch.hsr.ifs.iltis.core.core.tests.exception;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;


public class UnlessTest {

   @Test(expected = ILTISException.class)
   public void throwsIfNull() {
      ILTISException.Unless.notNull("", null);
   }

   @Test
   public void noThrowWhenNotNull() {
      ILTISException.Unless.notNull("", Integer.valueOf(42));
   }

   @Test(expected = ILTISException.class)
   public void throwsIfNotTrue() {
      ILTISException.Unless.isTrue("", false);
   }

   @Test
   public void noThrowWhenTrue() {
      ILTISException.Unless.isTrue("", true);
   }

   @Test(expected = ILTISException.class)
   public void throwsIfNotFalse() {
      ILTISException.Unless.isFalse("", true);
   }

   @Test
   public void noThrowWhenFalse() {
      ILTISException.Unless.isFalse("", false);
   }

   @Test(expected = ILTISException.class)
   public void throwsIfObjOfClassType() {
      final Integer i = 42;
      ILTISException.Unless.notAssignableFrom("", Number.class, i);
   }

   @Test
   public void noThrowWhenNotOfClassType() {
      final String s = "ILTIS";
      ILTISException.Unless.notInstanceOf("", s, Number.class);
   }

   @Test(expected = ILTISException.class)
   public void throwsIfObjNotOfClassType() {
      final String s = "ILTIS";
      ILTISException.Unless.instanceOf("", s, Number.class);
   }

   @Test
   public void noThrowWhenOfClassType() {
      final Integer i = 42;
      ILTISException.Unless.assignableFrom("", Number.class, i);
   }
}