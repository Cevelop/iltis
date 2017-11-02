package ch.hsr.ifs.iltis.core.functional;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public abstract class FunHelper {

   public static <T, R> R returnIfItIsElse(final Object arg, final Class<T> type, final Supplier<R> funThen, final Supplier<R> funElse) {
      return type.isInstance(arg) ? funThen.get() : funElse.get();
   }

   public static <T, R> R returnIfItIsElse(final Object arg, final Class<T> type, final Function<T, R> funThen, final Supplier<R> funElse) {
      return type.isInstance(arg) ? funThen.apply(type.cast(arg)) : funElse.get();
   }

   public static <T> void doIfItIs(final Object arg, final Class<T> type, final Consumer<T> funThen) {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      }
   }

   public static <T, E extends Exception> void doIfItIsT(final Object arg, final Class<T> type, final ThrowingConsumer<T, E> funThen) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      }
   }

   public static <T> void doIfItIsElse(final Object arg, final Class<T> type, final Consumer<T> funThen, final Runnable funElse) {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

   public static <T, E extends Exception> void doIfItIsTElse(final Object arg, final Class<T> type, final ThrowingConsumer<T, E> funThen,
            final Runnable funElse) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

   public static <T, E extends Exception> void doIfItIsElseT(final Object arg, final Class<T> type, final Consumer<T> funThen,
            final ThrowingRunnable<E> funElse) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

   public static <T, E extends Exception> void doIfItIsTElseT(final Object arg, final Class<T> type, final ThrowingConsumer<T, E> funThen,
            final ThrowingRunnable<E> funElse) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }
}
