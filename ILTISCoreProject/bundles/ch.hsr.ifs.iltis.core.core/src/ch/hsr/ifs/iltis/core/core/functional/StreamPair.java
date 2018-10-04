package ch.hsr.ifs.iltis.core.core.functional;

import ch.hsr.ifs.iltis.core.core.data.AbstractPair;


/**
 * A pair based on {@link AbstractPair} which is used by the convenience methods for Streams.
 * 
 * @author tstauber
 *
 * @param <T1>
 *        The type of the first value
 * @param <T2>
 *        The type of the second value
 */
public class StreamPair<T1, T2> extends AbstractPair<T1, T2> {

   StreamPair(final T1 first, final T2 second) {
      super(first, second);
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
      return second;
   }

   /**
    * Performs an unchecked cast to a StreamPair of newT1 and newT2
    * 
    * @param <newT1>
    *        The target type for the first value
    * @param <newT2>
    *        The target type for the second value
    * 
    * @return A {@code StreamPair<newT1, }{@code newT2>}
    * @throws ClassCastException
    *         If {@code first} is not castable to {@code newT1}
    *         or {@code second} is not castable to {@code newT2}
    */
   public <newT1, newT2> StreamPair<newT1, newT2> as() {
      return new StreamPair<>(Functional.as(first), Functional.as(second));
   }

   /**
    * Performs a checked cast to a StreamPair of clazz1 and clazz2
    * 
    * @param <newT1>
    *        The target type for the first value
    * @param <newT2>
    *        The target type for the second value
    * 
    * @param clazz1
    *        The target type for the first value
    * @param clazz2
    *        The target type for the second value
    * @return A {@code StreamPair<newT1, }{@code newT2>} or {@code null} if the cast is invalid
    */
   public <newT1, newT2> StreamPair<newT1, newT2> cast(final Class<newT1> clazz1, final Class<newT2> clazz2) {
      if (clazz1.isAssignableFrom(first.getClass()) && clazz2.isAssignableFrom(second.getClass())) {
         return new StreamPair<>(clazz1.cast(first), clazz2.cast(second));
      } else {
         return null;
      }
   }

}
