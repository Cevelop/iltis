package ch.hsr.ifs.iltis.core.exception;

public class ILTISException extends Exception {

   private static final long serialVersionUID = 1L;
   private Exception         originalException;

   //DOC missing
   public ILTISException(final String message) {
      super(message);
   }

   //DOC missing
   public ILTISException(final Exception originalException) {
      super(originalException);
      this.originalException = originalException;
   }

   //DOC missing
   public ILTISException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public ILTISException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }

   //DOC missing
   public void rethrow() throws Exception {
      if (originalException != null) {
         throw originalException;
      } else {
         throw this;
      }
   }

   //DOC missing
   public RuntimeException rethrowUnchecked() {
      return rethrowUncheckedG();
   }

   //DOC missing
   @SuppressWarnings("unchecked")
   private <T extends Exception> T rethrowUncheckedG() throws T {
      if (originalException != null) {
         throw (T) originalException;
      } else {
         throw (T) this;
      }
   }

   public static class Unless {

      public static void isTrue(final boolean expr, final String msg) {
         if (!expr) {
            throwWith(msg);
         }
      }

      public static void isFalse(final boolean expr, final String msg) {
         if (expr) {
            throwWith(msg);
         }
      }

      public static void notNull(final Object object, final String msg) {
         if (object == null) {
            throwWith(msg);
         }
      }

      public static <T> void instanceOf(final Object object, final Class<T> clazz, final String msg) {
         if (!checkIsInstance(object, clazz)) {
            throwWith(msg);
         }
      }

      public static <T> void notInstanceOf(final Object object, final Class<T> clazz, final String msg) {
         if (checkIsInstance(object, clazz)) {
            throwWith(msg);
         }
      }

      private static <T> boolean checkIsInstance(final Object object, final Class<T> clazz) {
         return clazz.isAssignableFrom(object.getClass());
      }

      private static void throwWith(final String message) {
         new ILTISException(message).rethrowUnchecked();
      }
   }

}
