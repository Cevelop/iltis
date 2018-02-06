package ch.hsr.ifs.iltis.core.exception;

/**
 * A specialized exception which can wrap other exceptions and rethrow them or itself as an unchecked exception.
 * 
 * @author tstauber
 *
 */
public class ILTISException extends Exception {

   private static final long serialVersionUID = 1L;
   private Exception         originalException;

   /**
    * Creates a new {@code ILTISException} containing a message
    * 
    * @param message
    *        The message displayed if the exception is thrown
    */
   public ILTISException(final String message) {
      super(message);
   }

   /**
    * Creates a new {@code ILTISException} wrapping another exception
    * 
    * @param originalException
    *        The original exception which will be wrapped
    */
   public ILTISException(final Exception originalException) {
      super(originalException);
      this.originalException = originalException;
   }

   /**
    * Creates a new {@code ILTISException} with a message and a cause
    * 
    * @param message
    *        The message displayed if the exception is thrown
    * @param cause
    *        The cause why this exception is thrown (follow up exceptions). {@code null} means the reason is unknown or non existent.
    */
   public ILTISException(final String message, final Throwable cause) {
      super(message, cause);
   }

   /**
    * Creates a new {@code ILTISException}
    * 
    * @param message
    *        The message displayed if the exception is thrown
    * @param cause
    *        The cause why this exception is thrown (follow up exceptions). {@code null} means the reason is unknown or non existent.
    * @param enableSuppression
    *        Whether or not suppression is enabled
    *        or disabled
    * @param writableStackTrace
    *        Whether or not the stack-trace should be writable
    */
   public ILTISException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   /**
    * If the {@code ILTISException} was created with a wrapped exception, this throws the wrapped exception, otherwise it throws itself.
    * 
    * @throws Exception
    */
   public void rethrow() throws Exception {
      if (originalException != null) {
         throw originalException;
      } else {
         throw this;
      }
   }

   /**
    * Rethrows the wrapped exception as an unchecked exception. If there is no wrapped exception, the ILTISException rethrows itself as an unchecked
    * exception.
    * 
    * @return Theoretically a {@code RuntimeException} but that never happens, as an unchecked Exception is thrown in this method.
    */
   public RuntimeException rethrowUnchecked() {
      return rethrowUncheckedG();
   }

   /**
    * Uses generic magic to "cast" a checked exception into an unchecked exception.
    * 
    * @return Theoretically a kind of {@link RuntimeException}, but this never happens, as the method always throws.
    * @throws T
    *         To make the magic work, this must be a {@code RuntimeException} or something which extends {@code RuntimeException}
    */
   @SuppressWarnings("unchecked")
   private <T extends Exception> T rethrowUncheckedG() throws T {
      if (originalException != null) {
         throw (T) originalException;
      } else {
         throw (T) this;
      }
   }

   /**
    * An utility class offering static methods to easily check a condition an subsequently throw an unchecked ILTISException, if the condition is
    * violated.
    * 
    * @author tstauber
    *
    */
   public static class Unless {

      /**
       * Throws an unchecked {@link ILTISException} with the message msg, if the passed expression is False
       * 
       * @param expr
       *        The expression to evaluate
       * @param msg
       *        The message
       */
      public static void isTrue(final boolean expr, final String msg) {
         if (!expr) {
            throwWith(msg);
         }
      }

      /**
       * Throws an unchecked {@link ILTISException} with the message msg, if the passed expression is True
       * 
       * @param expr
       *        The expression to evaluate
       * @param msg
       *        The message
       */
      public static void isFalse(final boolean expr, final String msg) {
         if (expr) {
            throwWith(msg);
         }
      }

      /**
       * Throws an unchecked {@link ILTISException} with the message msg, if the passed object is null
       * 
       * @param object
       *        The object to test
       * @param msg
       *        The message
       */
      public static void notNull(final Object object, final String msg) {
         if (object == null) {
            throwWith(msg);
         }
      }

      /**
       * Throws an unchecked {@link ILTISException} with the message msg, if the passed object is NOT assignable from clazz
       * @param clazz
       *        The class for which to test the object
       * @param object
       *        The object to test
       * @param msg
       *        The message
       */
      public static <T> void instanceOf(final Object object,final Class<T> clazz,  final String msg) {
         if (!checkIsInstance(object, clazz)) {
            throwWith(msg);
         }
      }

      /**
       * Throws an unchecked {@link ILTISException} with the message msg, if the passed object is assignable from clazz
       * @param clazz
       *        The class for which to test the object
       * @param object
       *        The object to test
       * @param msg
       *        The message
       */
      public static <T> void notInstanceOf(final Object object,final Class<T> clazz,  final String msg) {
         if (checkIsInstance(object, clazz)) {
            throwWith(msg);
         }
      }

      /**
       * Throws an unchecked {@link ILTISException} with the message msg, if the passed object is NOT assignable from clazz
       * 
       * @param object
       *        The object to test
       * @param clazz
       *        The class for which to test the object
       * @param msg
       *        The message
       */
      public static <T> void assignableFrom(final Class<T> clazz, final Object object, final String msg) {
         if (!checkIsAssignableFrom(object, clazz)) {
            throwWith(msg);
         }
      }

      /**
       * Throws an unchecked {@link ILTISException} with the message msg, if the passed object is assignable from clazz
       * 
       * @param object
       *        The object to test
       * @param clazz
       *        The class for which to test the object
       * @param msg
       *        The message
       */
      public static <T> void notAssignableFrom(final Class<T> clazz, final Object object, final String msg) {
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
      private static <T> boolean checkIsInstance(final Object object, final Class<T> clazz) {
         return clazz.isInstance(object.getClass());
      }

      /**
       * @param object
       *        The object to test
       * @param clazz
       *        The class to test for
       * @return {@code true} when clazz is assignable from the object
       */
      private static <T> boolean checkIsAssignableFrom(final Object object, final Class<T> clazz) {
         return clazz.isAssignableFrom(object.getClass());
      }

      /**
       * Throws a new ILTISException with the message as an unchecked exception
       * 
       * @param message
       *        The message
       */
      private static void throwWith(final String message) {
         new ILTISException(message).rethrowUnchecked();
      }
   }

}
