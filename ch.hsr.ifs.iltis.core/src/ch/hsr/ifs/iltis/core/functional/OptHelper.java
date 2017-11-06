package ch.hsr.ifs.iltis.core.functional;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.hsr.ifs.iltis.core.functional.functions.ThrowingConsumer;
import ch.hsr.ifs.iltis.core.functional.functions.ThrowingFunction;
import ch.hsr.ifs.iltis.core.functional.functions.ThrowingSupplier;


public abstract class OptHelper {

   public static <T, R> Optional<R> returnIfPresentElseEmpty(final Optional<T> optional, final Supplier<Optional<R>> fun) {
      return optional.isPresent() ? fun.get() : Optional.empty();
   }

   public static <T, R> Optional<R> returnIfPresentElseEmpty(final Optional<T> optional, final Function<T, Optional<R>> fun) {
      return optional.isPresent() ? fun.apply(optional.get()) : Optional.empty();
   }

   public static <T, R> R returnIfPresentElseNull(final Optional<T> optional, final Function<T, R> funThen) {
      return optional.isPresent() ? funThen.apply(optional.get()) : null;
   }

   public static <T, R> R returnIfPresentElse(final Optional<T> optional, final Function<T, R> funThen, final Supplier<R> funElse) {
      return optional.isPresent() ? funThen.apply(optional.get()) : funElse.get();
   }

   public static <T, R> R returnIfPresentElse(final Optional<T> optional, final Function<T, R> funThen, final R elseVal) {
      return optional.isPresent() ? funThen.apply(optional.get()) : elseVal;
   }

   public static <T, R, E extends Exception> R returnIfPresentTElse(final Optional<T> optional, final ThrowingFunction<T, R, E> funThen,
            final Supplier<R> funElse) throws E {
      return optional.isPresent() ? funThen.apply(optional.get()) : funElse.get();
   }

   public static <T, R, E extends Exception> R returnIfPresentElseT(final Optional<T> optional, final Function<T, R> funThen,
            final ThrowingSupplier<R, E> funElse) throws E {
      return optional.isPresent() ? funThen.apply(optional.get()) : funElse.get();
   }

   public static <T, R, E extends Exception> R returnIfPresentTElseT(final Optional<T> optional, final ThrowingFunction<T, R, E> funThen,
            final ThrowingSupplier<R, E> funElse) throws E {
      return optional.isPresent() ? funThen.apply(optional.get()) : funElse.get();
   }

   public static <T, R> R returnIfPresentElse(final Optional<T> optional, final Supplier<R> funThen, final Supplier<R> funElse) {
      return optional.isPresent() ? funThen.get() : funElse.get();
   }

   public static <T> void doIfPresent(final Optional<T> optional, final Consumer<T> funThen) {
      if (optional.isPresent()) {
         funThen.accept(optional.get());
      }
   }

   public static <T> void doIfPresentElse(final Optional<T> optional, final Consumer<T> funThen, final Runnable funElse) {
      if (optional.isPresent()) {
         funThen.accept(optional.get());
      } else {
         funElse.run();
      }
   }

   public static <T, E extends Exception> void doIfPresentT(final Optional<T> optional, final ThrowingConsumer<T, E> funThen) throws E {
      if (optional.isPresent()) {
         funThen.accept(optional.get());
      }
   }

   public static <T, E extends Exception> void doIfPresentElseT(final Optional<T> optional, final ThrowingConsumer<T, E> funThen,
            final Runnable funElse) throws E {
      if (optional.isPresent()) {
         funThen.accept(optional.get());
      } else {
         funElse.run();
      }
   }

}
