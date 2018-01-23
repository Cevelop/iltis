package ch.hsr.ifs.iltis.core.functional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import ch.hsr.ifs.iltis.core.functional.functions.ThrowingConsumer;
import ch.hsr.ifs.iltis.core.functional.functions.ThrowingRunnable;


public abstract class Functional {

   public static <A, B> Stream<StreamPair<A, B>> zip(final Stream<A> as, final Stream<B> bs) {
      final Iterator<A> i1 = as.iterator();
      final Iterator<B> i2 = bs.iterator();
      final Iterable<StreamPair<A, B>> i = () -> new Iterator<StreamPair<A, B>>() {

         @Override
         public boolean hasNext() {
            return i1.hasNext() && i2.hasNext();
         }

         @Override
         public StreamPair<A, B> next() {
            return new StreamPair<>(i1.next(), i2.next());
         }
      };
      return StreamSupport.stream(i.spliterator(), false);
   }

   public static <A, B> Stream<StreamPair<A, B>> zip(final Collection<A> as, final Collection<B> bs) {
      return zip(as.stream(), bs.stream());
   }

   public static <A, B> Stream<StreamPair<A, B>> zip(final A[] as, final B[] bs) {
      return zip(Arrays.stream(as), Arrays.stream(bs));
   }

   public static <A, B> Stream<StreamPair<A, B>> zip(final Map<A, B> map) {
      return zip(map.keySet(), map.values());
   }

   public static <A, B, C> Stream<StreamTripple<A, B, C>> zip(final Stream<A> as, final Stream<B> bs, final Stream<C> cs) {
      final Iterator<A> i1 = as.iterator();
      final Iterator<B> i2 = bs.iterator();
      final Iterator<C> i3 = cs.iterator();
      final Iterable<StreamTripple<A, B, C>> i = () -> new Iterator<StreamTripple<A, B, C>>() {

         @Override
         public boolean hasNext() {
            return i1.hasNext() && i2.hasNext();
         }

         @Override
         public StreamTripple<A, B, C> next() {
            return new StreamTripple<>(i1.next(), i2.next(), i3.next());
         }
      };
      return StreamSupport.stream(i.spliterator(), false);
   }

   public static <A, B, C> Stream<StreamTripple<A, B, C>> zip(final Collection<A> as, final Collection<B> bs, final Collection<C> cs) {
      return zip(as.stream(), bs.stream(), cs.stream());
   }

   public static <A, B, C> Stream<StreamTripple<A, B, C>> zip(final A[] as, final B[] bs, final C[] cs) {
      return zip(Arrays.stream(as), Arrays.stream(bs), Arrays.stream(cs));
   }

   /**
    * @since 0.1
    */
   public static <T> T asOrNull(final Class<T> c, final Object o) {
      return c.isInstance(o) ? c.cast(o) : null;
   }

   /**
    * @since 0.1
    */
   @SuppressWarnings("unchecked")
   public static <T> T as(final Object o) {
      return (T) o;
   }

   /**
    * @since 0.1
    */
   public static <T> void doIfItIs(final Object arg, final Class<T> type, final Consumer<T> funThen) {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      }
   }

   /**
    * @since 0.1
    */
   public static <T, R> R returnIfItIsElse(final Object arg, final Class<T> type, final Function<T, R> funThen, final Supplier<R> funElse) {
      return type.isInstance(arg) ? funThen.apply(type.cast(arg)) : funElse.get();
   }

   /**
    * @since 0.1
    */
   public static <T, R> R returnIfItIsElse(final Object arg, final Class<T> type, final Supplier<R> funThen, final Supplier<R> funElse) {
      return type.isInstance(arg) ? funThen.get() : funElse.get();
   }

   /**
    * @since 0.1
    */
   public static <T, E extends Exception> void doIfItIsT(final Object arg, final Class<T> type, final ThrowingConsumer<T, E> funThen) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      }
   }

   /**
    * @since 0.1
    */
   public static <T> void doIfItIsElse(final Object arg, final Class<T> type, final Consumer<T> funThen, final Runnable funElse) {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

   /**
    * @since 0.1
    */
   public static <T, E extends Exception> void doIfItIsTElse(final Object arg, final Class<T> type, final ThrowingConsumer<T, E> funThen,
            final Runnable funElse) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

   /**
    * @since 0.1
    */
   public static <T, E extends Exception> void doIfItIsElseT(final Object arg, final Class<T> type, final Consumer<T> funThen,
            final ThrowingRunnable<E> funElse) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

   /**
    * @since 0.1
    */
   public static <T, E extends Exception> void doIfItIsTElseT(final Object arg, final Class<T> type, final ThrowingConsumer<T, E> funThen,
            final ThrowingRunnable<E> funElse) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

}
