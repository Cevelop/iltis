package ch.hsr.ifs.iltis.core.functional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ch.hsr.ifs.iltis.core.data.Wrapper;
import ch.hsr.ifs.iltis.core.functional.functions.Function2;
import ch.hsr.ifs.iltis.core.functional.functions.ThrowingConsumer;
import ch.hsr.ifs.iltis.core.functional.functions.ThrowingRunnable;


/**
 * A utility class which provides static methods for operations used in functional programming.
 * 
 * @author tstauber
 *
 */
public abstract class Functional {

   /**
    * Used to zip two {@linkplain Stream}s into a single {@linkplain Stream} of {@linkplain StreamPair}. If the two input streams are of different
    * length, {@code null} will be used as default element.
    * 
    * @param <A>
    *        The first stream's element type
    * @param <B>
    *        The second stream's element type
    * 
    * @param as
    *        The first Stream
    * @param bs
    *        The second Stream
    * @return A {@linkplain Stream} of {@linkplain StreamPair}
    */
   public static <A, B> Stream<StreamPair<A, B>> zip(final Stream<A> as, final Stream<B> bs) {
      return zipWithDefaults(as, bs, () -> null, () -> null);
   }

   /**
    * Used to zip two {@linkplain Stream}s into a single {@linkplain Stream} of {@linkplain StreamPair}. If the two input streams are of different
    * length, the supplier defaultX will be executed to generate a default element.
    * 
    * @param <A>
    *        The first stream's element type
    * @param <B>
    *        The second stream's element type
    * 
    * @param as
    *        The first Stream
    * @param defaultA
    *        The generator to create a default element to use if the first stream is shorter than the second.
    * @param bs
    *        The second Stream
    * @param defaultB
    *        The generator to create a default element to use if the second stream is shorter than the first.
    * @return A {@linkplain Stream} of {@linkplain StreamPair}
    */
   public static <A, B> Stream<StreamPair<A, B>> zipWithDefaults(final Stream<A> as, final Stream<B> bs, final Supplier<A> defaultA,
            final Supplier<B> defaultB) {
      return zipWithDefaults(as, bs, (currentB) -> defaultA.get(), (currentA) -> defaultB.get());
   }

   /**
    * Used to zip two {@linkplain Stream}s into a single {@linkplain Stream} of {@linkplain StreamPair}. If the two input streams are of different
    * length, the function defaultX will be executed to generate a default element.
    * 
    * @param <A>
    *        The first stream's element type
    * @param <B>
    *        The second stream's element type
    * 
    * @param as
    *        The first Stream
    * @param defaultA
    *        The generator to create a default element to use if the first stream is shorter than the second. It can use the current element of the
    *        second stream.
    * @param bs
    *        The second Stream
    * @param defaultB
    *        The generator to create a default element to use if the second stream is shorter than the first. It can use the current element of the
    *        first stream.
    * @return A {@linkplain Stream} of {@linkplain StreamPair}
    */
   public static <A, B> Stream<StreamPair<A, B>> zipWithDefaults(final Stream<A> as, final Stream<B> bs, final Function<B, A> defaultA,
            final Function<A, B> defaultB) {
      final Spliterator<A> i1 = as.spliterator();
      final Spliterator<B> i2 = bs.spliterator();

      final Spliterator<StreamPair<A, B>> i = new Spliterator<StreamPair<A, B>>() {

         @Override
         public boolean tryAdvance(Consumer<? super StreamPair<A, B>> action) {
            final Wrapper<A> fst = new Wrapper<A>(null);
            i1.tryAdvance((it) -> fst.wrapped = it);
            final Wrapper<B> snd = new Wrapper<B>(null);
            i2.tryAdvance((it) -> snd.wrapped = it);
            if (fst.wrapped == null && snd.wrapped == null) {
               return false;
            }
            if (fst.wrapped == null) fst.wrapped = defaultA.apply(snd.wrapped);
            if (snd.wrapped == null) snd.wrapped = defaultB.apply(fst.wrapped);
            action.accept(new StreamPair<A, B>(fst.wrapped, snd.wrapped));
            return true;
         }

         @Override
         public Spliterator<StreamPair<A, B>> trySplit() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public long estimateSize() {
            return Math.max(i1.estimateSize(), i2.estimateSize());
         }

         @Override
         public int characteristics() {
            return i1.characteristics() & i2.characteristics();
         }
      };
      return StreamFactory.stream(i);
   }

   /**
    * Used to zip two {@linkplain Collection}s into a single {@linkplain Stream} of {@linkplain StreamPair}.
    * 
    * @see #zipWithDefaults(Stream, Stream, Supplier, Supplier)
    * 
    * @param <A>
    *        The first collection's element type
    * @param <B>
    *        The second collection's element type
    * 
    * @param as
    *        The first values
    * @param bs
    *        The second values
    * @return A {@linkplain Stream} of {@linkplain StreamPair}
    */
   public static <A, B> Stream<StreamPair<A, B>> zip(final Collection<A> as, final Collection<B> bs) {
      return zip(as.stream(), bs.stream());
   }

   /**
    * Used to zip two {@linkplain X[]} into a single {@linkplain Stream} of {@linkplain StreamPair}.
    * 
    * @see #zipWithDefaults(Stream, Stream, Supplier, Supplier)
    * 
    * @param <A>
    *        The first array's element type
    * @param <B>
    *        The second array's element type
    * 
    * @param as
    *        The first values
    * @param bs
    *        The second values
    * @return A {@linkplain Stream} of {@linkplain StreamPair}
    */
   public static <A, B> Stream<StreamPair<A, B>> zip(final A[] as, final B[] bs) {
      return zip(Arrays.stream(as), Arrays.stream(bs));
   }

   /**
    * Used to zip a {@linkplain Map} into a single {@linkplain Stream} of {@linkplain StreamPair}.
    * 
    * @see #zipWithDefaults(Stream, Stream, Supplier, Supplier)
    * @see StreamUtil#toMap()
    * 
    * @param <A>
    *        The map's key type
    * @param <B>
    *        The map's value type
    * 
    * @param map
    *        The map
    * @return A {@linkplain Stream} of {@linkplain StreamPair}
    */
   public static <A, B> Stream<StreamPair<A, B>> zip(final Map<A, B> map) {
      return zip(map.keySet(), map.values());
   }

   /**
    * Used to zip three {@linkplain Stream}s into a single {@linkplain Stream} of {@linkplain StreamTriple}. If the three input streams are of
    * different length, {@code null} will be used as default element.
    * 
    * @param <A>
    *        The first stream's element type
    * @param <B>
    *        The second stream's element type
    * @param <C>
    *        The third stream's element type
    * 
    * @param as
    *        The first Stream
    * @param bs
    *        The second Stream
    * @param cs
    *        The third Stream
    * @return A {@linkplain Stream} of {@linkplain StreamTriple}
    */
   public static <A, B, C> Stream<StreamTriple<A, B, C>> zip(final Stream<A> as, final Stream<B> bs, final Stream<C> cs) {
      return zipWithDefaults(as, bs, cs, () -> null, () -> null, () -> null);
   }

   /**
    * Used to zip three {@linkplain Stream}s into a single {@linkplain Stream} of {@linkplain StreamTriple}. If the three input streams are of
    * different length, defaultX will be used to generate a default element.
    * 
    * @param <A>
    *        The first stream's element type
    * @param <B>
    *        The second stream's element type
    * @param <C>
    *        The third stream's element type
    * 
    * @param as
    *        The first Stream
    * @param defaultA
    *        The generator to create a default element to use if the first stream is shorter than the second, or third.
    * @param bs
    *        The second Stream
    * @param defaultB
    *        The generator to create a default element to use if the first stream is shorter than the first, or third.
    * @param cs
    *        The third Stream
    * @param defaultC
    *        The generator to create a default element to use if the first stream is shorter than the first, or second.
    * @return A {@linkplain Stream} of {@linkplain StreamTriple}
    */
   public static <A, B, C> Stream<StreamTriple<A, B, C>> zipWithDefaults(final Stream<A> as, final Stream<B> bs, final Stream<C> cs,
            final Supplier<A> defaultA, final Supplier<B> defaultB, final Supplier<C> defaultC) {
      return zipWithDefaults(as, bs, cs, (currentB, currentC) -> defaultA.get(), (currentA, currentC) -> defaultB.get(), (currentA,
               currentB) -> defaultC.get());
   }

   /**
    * Used to zip three {@linkplain Stream}s into a single {@linkplain Stream} of {@linkplain StreamTriple}. If the three input streams are of
    * different length, defaultX will be used to generate a default element.
    * 
    * @param <A>
    *        The first stream's element type
    * @param <B>
    *        The second stream's element type
    * @param <C>
    *        The third stream's element type
    * 
    * @param as
    *        The first Stream
    * @param defaultA
    *        The generator to create a default element to use if the first stream is shorter than the second, or third. It can use the current element
    *        of the
    *        second, and third stream.
    * @param bs
    *        The second Stream
    * @param defaultB
    *        The generator to create a default element to use if the first stream is shorter than the first, or third. It can use the current element
    *        of the
    *        first, and third stream.
    * @param cs
    *        The third Stream
    * @param defaultC
    *        The generator to create a default element to use if the first stream is shorter than the first, or second. It can use the current element
    *        of the
    *        first, and second stream.
    */
   public static <A, B, C> Stream<StreamTriple<A, B, C>> zipWithDefaults(final Stream<A> as, final Stream<B> bs, final Stream<C> cs,
            final Function2<B, C, A> defaultA, final Function2<A, C, B> defaultB, final Function2<A, B, C> defaultC) {
      final Spliterator<A> i1 = as.spliterator();
      final Spliterator<B> i2 = bs.spliterator();
      final Spliterator<C> i3 = cs.spliterator();

      final Spliterator<StreamTriple<A, B, C>> i = new Spliterator<StreamTriple<A, B, C>>() {

         @Override
         public boolean tryAdvance(Consumer<? super StreamTriple<A, B, C>> action) {
            final Wrapper<A> fst = new Wrapper<A>(null);
            i1.tryAdvance((it) -> fst.wrapped = it);
            final Wrapper<B> snd = new Wrapper<B>(null);
            i2.tryAdvance((it) -> snd.wrapped = it);
            final Wrapper<C> trd = new Wrapper<C>(null);
            i3.tryAdvance((it) -> trd.wrapped = it);
            if (fst.wrapped == null && snd.wrapped == null && trd.wrapped == null) {
               return false;
            }
            if (fst.wrapped == null) fst.wrapped = defaultA.apply(snd.wrapped, trd.wrapped);
            if (snd.wrapped == null) snd.wrapped = defaultB.apply(fst.wrapped, trd.wrapped);
            if (trd.wrapped == null) trd.wrapped = defaultC.apply(fst.wrapped, snd.wrapped);
            action.accept(new StreamTriple<A, B, C>(fst.wrapped, snd.wrapped, trd.wrapped));
            return true;
         }

         @Override
         public Spliterator<StreamTriple<A, B, C>> trySplit() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public long estimateSize() {
            return Math.max(i1.estimateSize(), Math.max(i2.estimateSize(), i3.estimateSize()));
         }

         @Override
         public int characteristics() {
            return i1.characteristics() & i2.characteristics() & i3.characteristics();
         }
      };
      return StreamFactory.stream(i);
   }

   /**
    * Used to zip three {@linkplain Collection}s into a single {@linkplain Stream} of {@linkplain StreamTriple}.
    * 
    * @see #zipWithDefaults(Stream, Stream, Stream, Function2, Function2, Function2)
    * 
    * @param <A>
    *        The first Collection's elements' type
    * @param <B>
    *        The second Collection's elements' type
    * @param <C>
    *        The third Collection's elements' type
    * 
    * @param as
    *        The first Collection
    * @param bs
    *        The second Collection
    * @param cs
    *        The third Collection
    * 
    * @return A {@linkplain Stream} of {@linkplain StreamTriple}
    */
   public static <A, B, C> Stream<StreamTriple<A, B, C>> zip(final Collection<A> as, final Collection<B> bs, final Collection<C> cs) {
      return zip(as.stream(), bs.stream(), cs.stream());
   }

   /**
    * Used to zip three {@linkplain X[]} into a single {@linkplain Stream} of {@linkplain StreamTriple}.
    * 
    * @see #zipWithDefaults(Stream, Stream, Stream, Function2, Function2, Function2)
    * 
    * @param <A>
    *        The first array's elements' type
    * @param <B>
    *        The second array's elements' type
    * @param <C>
    *        The third array's elements' type
    * 
    * @param as
    *        The first array
    * @param bs
    *        The second array
    * @param cs
    *        The third array
    * 
    * @return A {@linkplain Stream} of {@linkplain StreamTriple}
    */
   public static <A, B, C> Stream<StreamTriple<A, B, C>> zip(final A[] as, final B[] bs, final C[] cs) {
      return zip(Arrays.stream(as), Arrays.stream(bs), Arrays.stream(cs));
   }

   /**
    * Used to apply a mapping to an array. As no arrays of generic types can be created (arrays are covariant), the method reference to an array
    * constructor must be passed.
    * 
    * <pre>
    * 
    * {@code map(fooArray, (foo) -> foo.extractBar, Bar[]::new);}
    * </pre>
    * 
    * @param <A>
    *        The input array's elements' type
    * @param <B>
    *        The resulting array's elements' type
    * 
    * @param as
    *        The input array
    * @param mapping
    *        The {@linkplain Function} applied to each value
    * @param generator
    *        A generator to create a new array of the target type
    * @return A new array of the target type {@linkplain B}
    */
   public static <A, B> B[] map(final A[] as, Function<A, B> mapping, final IntFunction<B[]> generator) {
      return Arrays.stream(as).map(mapping).toArray(generator);
   }

   /**
    * Used to cast an element to the target type. If this cast is invalid, null will be returned
    * 
    * @param <T>
    *        The Type of clazz
    * @param type
    *        The target type
    * @param o
    *        The object to cast
    * @returns The object o casted to {@link T} if it is an instance of the target type.
    * @returns Else {@code null}
    */
   public static <T> T asOrNull(final Class<T> type, final Object o) {
      return type.isInstance(o) ? type.cast(o) : null;
   }

   /**
    * Used to perform an unchecked cast to {@linkplain T}.
    * 
    * @param o
    *        The object to cast
    * @return The object casted to {@linkplain T}
    * @throws ClassCastException
    *         If the attempted cast failed
    */
   @SuppressWarnings("unchecked")
   public static <T> T as(final Object o) {
      return (T) o;
   }

   /**
    * Depending if the passed object is an instance of the target type, one of funThen and funElse will be executed and the result will be returned.
    * 
    * @param <T>
    *        The target type
    * @param <R>
    *        The return type
    * 
    * @param arg
    *        The object to test
    * @param type
    *        The target type for which to test
    * @param funThen
    *        The function to execute, if the cast was successful (takes the casted object as an argument)
    * @param funElse
    *        The function to execute, if the cast was unsuccessful
    * @return An object of type {@linkplain R}
    */
   public static <T, R> R returnIfItIsElse(final Object arg, final Class<T> type, final Function<T, R> funThen, final Supplier<R> funElse) {
      return type.isInstance(arg) ? funThen.apply(type.cast(arg)) : funElse.get();
   }

   /**
    * Depending if the passed object is an instance of the target type, one of funThen and funElse will be executed and the result will be returned.
    * 
    * @param <T>
    *        The target type
    * @param <R>
    *        The return type
    * 
    * @param arg
    *        The object to test
    * @param type
    *        The target type for which to test
    * @param funThen
    *        The function to execute, if the cast was successful
    * @param funElse
    *        The function to execute, if the cast was unsuccessful
    * @return An object of type {@linkplain R}
    */
   public static <T, R> R returnIfItIsElse(final Object arg, final Class<T> type, final Supplier<R> funThen, final Supplier<R> funElse) {
      return type.isInstance(arg) ? funThen.get() : funElse.get();
   }

   /**
    * Executes a function, if the passed object is instance of the target type. If the object is an instance of the target type, it will be casted and
    * passed to the function.
    * 
    * @param <T>
    *        The target type
    * 
    * @param arg
    *        The object to test
    * @param type
    *        The target type for which to test
    * @param funThen
    *        The function to execute if the cast was successful
    */
   public static <T> void doIfItIs(final Object arg, final Class<T> type, final Consumer<T> funThen) {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      }
   }

   /**
    * Executes a function, if the passed object is instance of the target type. If the object is an instance of the target type, it will be casted and
    * passed to the function. The passed function can throw.
    * 
    * @param <T>
    *        The target type
    * @param <E>
    *        The exception type
    * 
    * @param arg
    *        The object to test
    * @param type
    *        The target type for which to test
    * @param funThen
    *        The function to execute if the cast was successful
    * @throws E
    *         pass-through from funThen
    */
   public static <T, E extends Exception> void doIfItIsT(final Object arg, final Class<T> type, final ThrowingConsumer<T, E> funThen) throws E {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      }
   }

   /**
    * Depending if the passed object is an instance of the target type, one of funThen and funElse will be executed.
    * 
    * @param <T>
    *        The target type
    * 
    * @param arg
    *        The object to test
    * @param type
    *        The target type for which to test
    * @param funThen
    *        The function to execute, if the cast was successful
    * @param funElse
    *        The function to execute, if the cast was unsuccessful
    */
   public static <T> void doIfItIsElse(final Object arg, final Class<T> type, final Consumer<T> funThen, final Runnable funElse) {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

   /**
    * Depending if the passed object is an instance of the target type, one of funThen and funElse will be executed.
    * 
    * @param <T>
    *        The target type
    * @param <E>
    *        The exception type
    * 
    * @param arg
    *        The object to test
    * @param type
    *        The target type for which to test
    * @param funThen
    *        The function to execute, if the cast was successful
    * @param funElse
    *        The function to execute, if the cast was unsuccessful
    * @throws E
    *         pass-through from funThen
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
    * Depending if the passed object is an instance of the target type, one of funThen and funElse will be executed.
    * 
    * @param <T>
    *        The target type
    * @param <E>
    *        The exception type
    * 
    * @param arg
    *        The object to test
    * @param type
    *        The target type for which to test
    * @param funThen
    *        The function to execute, if the cast was successful
    * @param funElse
    *        The function to execute, if the cast was unsuccessful
    * @throws E
    *         pass-through from funElse
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
    * Depending if the passed object is an instance of the target type, one of funThen and funElse will be executed.
    * 
    * @param <T>
    *        The target type
    * @param <E1>
    *        The "then" exception type
    * @param <E2>
    *        The "else" exception type
    * 
    * @param arg
    *        The object to test
    * @param type
    *        The target type for which to test
    * @param funThen
    *        The function to execute, if the cast was successful
    * @param funElse
    *        The function to execute, if the cast was unsuccessful
    * @throws E1
    *         pass-through from funThen
    * @throws E2
    *         pass-through from funElse
    */
   public static <T, E1 extends Exception, E2 extends Exception> void doIfItIsTElseT(final Object arg, final Class<T> type,
            final ThrowingConsumer<T, E1> funThen, final ThrowingRunnable<E2> funElse) throws E1, E2 {
      if (type.isInstance(arg)) {
         funThen.accept(type.cast(arg));
      } else {
         funElse.run();
      }
   }

}
