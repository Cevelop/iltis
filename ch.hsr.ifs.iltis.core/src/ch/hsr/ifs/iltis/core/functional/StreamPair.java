package ch.hsr.ifs.iltis.core.functional;

import ch.hsr.ifs.iltis.core.data.AbstractPair;


public class StreamPair<T1, T2> extends AbstractPair<T1, T2> {

   StreamPair(final T1 first, final T2 second) {
      super(first, second);
   }

   public T1 first() {
      return first;
   }

   public T2 second() {
      return second;
   }

   public <newT1, newT2> StreamPair<newT1, newT2> as() {
      return new StreamPair<>(Functional.as(first), Functional.as(second));
   }

   public <newT1, newT2> StreamPair<newT1, newT2> cast(final Class<newT1> clazz1, final Class<newT2> clazz2) {
      if (clazz1.isAssignableFrom(first.getClass()) && clazz2.isAssignableFrom(second.getClass())) {
         return new StreamPair<>(clazz1.cast(first), clazz2.cast(second));
      } else {
         return null;
      }
   }

}
