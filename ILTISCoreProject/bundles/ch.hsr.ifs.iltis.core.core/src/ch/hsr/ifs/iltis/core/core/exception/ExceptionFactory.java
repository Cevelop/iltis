package ch.hsr.ifs.iltis.core.core.exception;

import ch.hsr.ifs.iltis.core.core.functional.functions.Function;
import ch.hsr.ifs.iltis.core.core.exception.ExceptionFactory;
import ch.hsr.ifs.iltis.core.core.exception.ILTISException;


/**
 * An utility class offering static methods to easily check a condition an subsequently throw an unchecked ILTISException, if the condition is
 * violated.
 * 
 * @author tstauber
 *
 */
public class ExceptionFactory {

   private final Function<String, ? extends ILTISException> constructor;

   public ExceptionFactory(Function<String, ? extends ILTISException> constructor) {
      this.constructor = constructor;
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if expected does <b>not</b> equals actual
    * 
    * @param msg
    *        The message
    * @param expected
    *        The expected value
    * @param actual
    *        The actual value
    */
   @SuppressWarnings("unlikely-arg-type")
   public <T1, T2> void isEqual(final String msg, final T1 expected, final T2 actual) {
      if (!expected.equals(actual)) {
         throwWith(msg);
      }
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if expected equals actual
    * 
    * @param msg
    *        The message
    * @param expected
    *        The expected value
    * @param actual
    *        The actual value
    */
   @SuppressWarnings("unlikely-arg-type")
   public <T1, T2> void isUnEqual(final String msg, final T1 expected, final T2 actual) {
      if (expected.equals(actual)) {
         throwWith(msg);
      }
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if the passed expression is False
    * 
    * @param msg
    *        The message
    * @param expr
    *        The expression to evaluate
    */
   public void isTrue(final String msg, final boolean expr) {
      if (!expr) {
         throwWith(msg);
      }
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if the passed expression is True
    * 
    * @param msg
    *        The message
    * @param expr
    *        The expression to evaluate
    */
   public void isFalse(final String msg, final boolean expr) {
      if (expr) {
         throwWith(msg);
      }
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if the passed object is null
    * 
    * @param msg
    *        The message
    * @param object
    *        The object to test
    */
   public void notNull(final String msg, final Object object) {
      if (object == null) {
         throwWith(msg);
      }
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if the passed object is NOT assignable from clazz
    * 
    * @param msg
    *        The message
    * @param object
    *        The object to test
    * @param clazz
    *        The class for which to test the object
    */
   public <T> void instanceOf(final String msg, final Object object, final Class<T> clazz) {
      if (!checkIsInstance(object, clazz)) {
         throwWith(msg);
      }
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if the passed object is assignable from clazz
    * 
    * @param msg
    *        The message
    * @param object
    *        The object to test
    * @param clazz
    *        The class for which to test the object
    */
   public <T> void notInstanceOf(final String msg, final Object object, final Class<T> clazz) {
      if (checkIsInstance(object, clazz)) {
         throwWith(msg);
      }
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if the passed object is NOT assignable from clazz
    * 
    * @param msg
    *        The message
    * @param clazz
    *        The class for which to test the object
    * @param object
    *        The object to test
    */
   public <T> void assignableFrom(final String msg, final Class<T> clazz, final Object object) {
      if (!checkIsAssignableFrom(object, clazz)) {
         throwWith(msg);
      }
   }

   /**
    * Throws an unchecked {@link ExceptionFactory} with the message msg, if the passed object is assignable from clazz
    * 
    * @param msg
    *        The message
    * @param clazz
    *        The class for which to test the object
    * @param object
    *        The object to test
    */
   public <T> void notAssignableFrom(final String msg, final Class<T> clazz, final Object object) {
      if (checkIsAssignableFrom(object, clazz)) {
         throwWith(msg);
      }
   }

   /**
    * @param object
    *        The object to test
    * @param clazz
    *        The class to test for
    * @return {@code true} when the object is instance of clazz
    */
   private <T> boolean checkIsInstance(final Object object, final Class<T> clazz) {
      return clazz.isInstance(object.getClass());
   }

   /**
    * @param object
    *        The object to test
    * @param clazz
    *        The class to test for
    * @return {@code true} when clazz is assignable from the object
    */
   private <T> boolean checkIsAssignableFrom(final Object object, final Class<T> clazz) {
      return clazz.isAssignableFrom(object.getClass());
   }

   /**
    * Throws a new ILTISException with the message as an unchecked exception
    * 
    * @param message
    *        The message
    */
   private void throwWith(final String message) {
      throw constructor.apply(message);
   }

}
