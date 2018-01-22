package ch.hsr.ifs.iltis.core.functional;

import ch.hsr.ifs.iltis.core.data.AbstractPair;


public class StreamTripple<T1, T2, T3> extends AbstractPair<T1, TrippleHelper<T2, T3>> {

   StreamTripple(final T1 first, final T2 second, final T3 third) {
      super(first, new TrippleHelper<>(second, third));
   }

   public T1 first() {
      return first;
   }

   public T2 second() {
      return second.second();
   }

   public T3 third() {
      return second.third();
   }

   public <newT1, newT2, newT3> StreamTripple<newT1, newT2, newT3> as() {
      return new StreamTripple<>(Functional.as(first()), Functional.as(second()), Functional.as(third()));
   }

}



class TrippleHelper<T2, T3> extends AbstractPair<T2, T3> {

   public TrippleHelper(final T2 second, final T3 third) {
      super(second, third);
   }

   public T2 second() {
      return first;
   }

   public T3 third() {
      return second;
   }

}
