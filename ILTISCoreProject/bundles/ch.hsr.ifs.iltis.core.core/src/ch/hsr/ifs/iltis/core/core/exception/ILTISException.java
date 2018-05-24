package ch.hsr.ifs.iltis.core.core.exception;

import ch.hsr.ifs.iltis.core.core.exception.ExceptionFactory;
import ch.hsr.ifs.iltis.core.core.exception.ILTISException;

/**
 * A specialized exception which can wrap other exceptions and rethrow them or itself as an unchecked exception.
 * 
 * @author tstauber
 *
 */
public class ILTISException extends RuntimeException {

   public static ExceptionFactory Unless           = new ExceptionFactory(ILTISException::new);
   protected static final long    serialVersionUID = 0x1000000;
   private Exception              originalException;

   /**
    * Creates a new {@code ILTISException} containing a message
    * 
    * @param message
    *        The message displayed if the exception is thrown
    */
   public ILTISException(final String message) {
      this(message, true);
   }

   /**
    * Creates a new {@code ILTISException} containing a message but without a stack-trace if createStacktrace is false
    * 
    * @param message
    *        The message displayed if the exception is thrown
    * @param createStacktrace
    *        Whether to create a stack-trace
    */
   protected ILTISException(final String message, final boolean createStacktrace) {
      super(message, null, true, createStacktrace);
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

   public static ILTISException wrap(final Exception originalException) {
      return new ILTISException(originalException);
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
      return genericCastAndThrowException();
   }

   /**
    * Rethrows the wrapped exception as a checked exception. If there is no wrapped exception, the ILTISException rethrows itself as a checked
    * exception.
    * 
    * @return Theoretically an {@code Exception} but that never happens, as an checked Exception is thrown in this method.
    */
   public Exception rethrowChecked() {
      return genericCastAndThrowException();
   }

   /**
    * Uses generic magic to "cast" a checked exception into an unchecked exception.
    * 
    * @return Theoretically a kind of {@link RuntimeException}, but this never happens, as the method always throws.
    * @throws T
    *         To make the magic work, this must be a {@code RuntimeException} or something which extends {@code RuntimeException}
    */
   @SuppressWarnings("unchecked")
   private <T extends Exception> T genericCastAndThrowException() throws T {
      if (originalException != null) {
         throw (T) originalException;
      } else {
         throw (T) this;
      }
   }

}
