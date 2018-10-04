package ch.hsr.ifs.iltis.core.core.functional;

import ch.hsr.ifs.iltis.core.core.data.AbstractPair;


/**
 * A triple based on {@link AbstractPair} which is used by the convenience methods for Streams.
 * 
 * @author tstauber
 *
 * @param <T1>
 *        The type of the first value
 * @param <T2>
 *        The type of the second value
 * @param <T3>
 *        The type of the third value
 */
public class StreamTriple<T1, T2, T3> extends AbstractPair<T1, StreamPair<T2, T3>> {

   StreamTriple(final T1 first, final T2 second, final T3 third) {
      super(first, new StreamPair<>(second, third));
   }

   /**
    * @return The first element
    */
   public T1 first() {
      return first;
   }

   /**
    * @return The second element
    */
   public T2 second() {
      return second.first();
   }

   /**
    * @return The third element
    */
   public T3 third() {
      return second.second();
   }

   /**
    * Performs an unchecked cast to a StreamTriple of newT1, newT2, and newT3
    * 
    * @param <newT1>
    *        The target type for the first value
    * @param <newT2>
    *        The target type for the second value
    * @param <newT3>
    *        The target type for the third value
    * 
    * @return A {@code StreamTriple<newT1, }{@code newT2, newT3>}
    * @throws ClassCastException
    *         If {@code first} is not castable to {@code newT1}
    *         or {@code second} is not castable to {@code newT2}
    *         or {@code third} is not castable to {@code newT3}
    */
   public <newT1, newT2, newT3> StreamTriple<newT1, newT2, newT3> as() {
      return new StreamTriple<>(Functional.as(first()), Functional.as(second()), Functional.as(third()));
   }

   /**
    * Performs a checked cast to a StreamTriple of clazz1, clazz2, and clazz3
    * 
    * @param <newT1>
    *        The target type for the first value
    * @param <newT2>
    *        The target type for the second value
    * @param <newT3>
    *        The target type for the third value
    * 
    * @param clazz1
    *        The target type for the first value
    * @param clazz2
    *        The target type for the second value
    * @param clazz3
    *        The target type for the third value
    * @return A {@code StreamTriple<newT1, }{@code newT2, newT3>} or {@code null} if the cast is invalid
    */
   public <newT1, newT2, newT3> StreamTriple<newT1, newT2, newT3> cast(final Class<newT1> clazz1, final Class<newT2> clazz2,
            final Class<newT3> clazz3) {
      if (clazz1.isAssignableFrom(first().getClass()) && clazz2.isAssignableFrom(second().getClass()) && clazz3.isAssignableFrom(third()
               .getClass())) {
         return new StreamTriple<>(clazz1.cast(first()), clazz2.cast(second()), clazz3.cast(third()));
      } else {
         return null;
      }
   }

}
