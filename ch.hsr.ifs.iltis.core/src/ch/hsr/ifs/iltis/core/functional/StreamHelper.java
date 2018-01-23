package ch.hsr.ifs.iltis.core.functional;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * @since 0.1
 */
public abstract class StreamHelper {

   public static <T> Stream<T> from(final Enumeration<T> enumeration) {
//      TODO in java 9 use enumeration.asIterator()
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
}
