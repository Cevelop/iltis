package ch.hsr.ifs.iltis.core.functional;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public abstract class StreamFactory {

   public static <T> Stream<T> from(final Enumeration<T> enumeration) {
      //      TODO once using java 9 replace this body with the commented statement
      //      return from(enumeration.asIterator());
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {

         public T next() {
            return enumeration.nextElement();
         }

         public boolean hasNext() {
            return enumeration.hasMoreElements();
         }

         public void forEachRemaining(final Consumer<? super T> action) {
            while (enumeration.hasMoreElements())
               action.accept(enumeration.nextElement());
         }
      }, Spliterator.ORDERED), false);
   }

   public static <T> Stream<T> from(final Iterable<T> iterable) {
      return StreamSupport.stream(iterable.spliterator(), false);
   }

}
