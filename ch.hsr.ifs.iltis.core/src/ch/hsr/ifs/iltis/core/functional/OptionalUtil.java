package ch.hsr.ifs.iltis.core.functional;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.hsr.ifs.iltis.core.functional.functions.ThrowingConsumer;


/**
 * A utility class which provides static methods for operations on {@linkplain Optional} which exceed the possibilities implemented on Optional.
 * 
 * @author tstauber
 *
 */
public class OptionalUtil<T> {

   private Optional<T> optional;

   private OptionalUtil(Optional<T> optional) {
      this.optional = optional;
   }

   public static <T> OptionalUtil<T> of(Optional<T> optional) {
      return new OptionalUtil<T>(optional);
   }

   public Optional<T> get() {
      return optional;
   }

   public <R> OptionalUtil<R> map(final Function<Optional<T>, Optional<R>> mapping) {
      return OptionalUtil.of(mapping.apply(optional));
   }

   /**
    * Executes the passed function, if the optional has a value present.
    * 
    * @param <T>
    *        The type wrapped by the Optional
    * @param funThen
    *        A function which takes the extracted value of the passed Optional
    */
   public OptionalUtil<T> doIfPresent(final Consumer<T> funThen) {
      optional.ifPresent(funThen::accept);
      return this;
   }

   /**
    * Executes the passed function, if the optional has no value present.
    * 
    * @param <T>
    *        The type wrapped by the Optional
    * @param funThen
    *        A function which takes the extracted value of the passed Optional
    */
   public OptionalUtil<T> doIfNotPresent(final Supplier<T> funThen) {
      if (!optional.isPresent()) funThen.get();
      return this;
   }

   public OptionalUtil<T> doIfNotPresent(final Runnable funThen) {
      if (!optional.isPresent()) funThen.run();
      return this;
   }

   /**
    * Executes the passed function, if the optional has a value present.
    * 
    * @param <T>
    *        The type wrapped by the Optional
    * @param <E>
    *        The exception type
    * @param funThen
    *        A function which can throw and takes the extracted value of the passed Optional
    * @throws E
    *         pass-through from funThen
    */
   public <E extends Exception> OptionalUtil<T> doIfPresentT(final ThrowingConsumer<T, E> funThen) throws E {
      if (optional.isPresent()) funThen.accept(optional.get());
      return this;
   }

   /**
    * Executes the passed function, if the optional has no value present.
    * 
    * @param <T>
    *        The type wrapped by the Optional
    * @param <E>
    *        The exception type
    * @param funThen
    *        A function which can throw and takes the extracted value of the passed Optional
    * @throws E
    *         pass-through from funThen
    */
   public <E extends Exception> OptionalUtil<T> doIfNotPresentT(final ThrowingConsumer<T, E> funThen) throws E {
      if (!optional.isPresent()) funThen.accept(optional.get());
      return this;
   }

}
