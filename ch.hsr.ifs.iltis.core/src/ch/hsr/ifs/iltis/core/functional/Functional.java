package ch.hsr.ifs.iltis.core.functional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


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

}
