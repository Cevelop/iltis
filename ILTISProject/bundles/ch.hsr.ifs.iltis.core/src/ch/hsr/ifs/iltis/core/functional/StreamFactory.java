package ch.hsr.ifs.iltis.core.functional;

import static ch.hsr.ifs.iltis.core.collections.CollectionUtil.array;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * A factory which provides static methods for creating a {@linkplain Stream}
 *
 * @author tstauber
 *
 */
public abstract class StreamFactory {

    /**
     * Creates a {@linkplain Stream} from an {@linkplain Enumeration}
     *
     * @param <T>
     * The {@code Stream} and {@code Enumeration} type
     * @param enumeration
     * The Enumeration
     * @return A Stream containing the elements of the Enumeration
     */
    public static <T> Stream<T> stream(final Enumeration<T> enumeration) {
        //      FIXME once using java 9 replace this body with the commented statement
        //      return from(enumeration.asIterator());
        return stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {

            @Override
            public T next() {
                return enumeration.nextElement();
            }

            @Override
            public boolean hasNext() {
                return enumeration == null ? false : enumeration.hasMoreElements();
            }

            @Override
            public void forEachRemaining(final Consumer<? super T> action) {
                while (enumeration.hasMoreElements())
                    action.accept(enumeration.nextElement());
            }
        }, Spliterator.ORDERED));
    }

    /**
     * Creates a {@linkplain Stream} from an {@linkplain Iterator}
     *
     * @param <T>
     * The {@code Stream} and {@code Iterator} type
     * @param iterator
     * The Iterator
     * @return A Stream containing all the elements of the Iterator
     */
    public static <T> Stream<T> stream(final Iterator<T> iterator) {
        return stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED));
    }

    /**
     * Creates a {@linkplain Stream} from an {@linkplain Spliterator}
     *
     * @param <T>
     * The {@code Stream} and {@code Spliterator} type
     * @param spliterator
     * The Spliterator
     * @return A Stream containing all the elements of the Spliterator
     */
    public static <T> Stream<T> stream(final Spliterator<T> spliterator) {
        return StreamSupport.stream(spliterator, false);
    }

    /**
     * Creates a {@linkplain Stream} from an varargs
     *
     * @param <T>
     * The {@code Stream} and element array type
     * @param elements
     * The elements
     * @return A Stream containing the elements
     */
    @SafeVarargs
    public static <T> Stream<T> stream(final T... elements) {
        return Arrays.stream(array(elements));
    }

    /**
     * Creates a concatenated stream
     *
     * @param <T>
     * The {@code Stream} type
     * @param first
     * The first stream
     * @param others
     * The other streams
     * @return A concatenated stream
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <T> Stream<T> stream(final Stream<? extends T> first, final Stream<? extends T>... others) {
        Stream<? extends T> concatenated = first;
        for (final Stream<? extends T> s : others) {
            concatenated = Stream.concat(first, s);
        }
        return (Stream<T>) concatenated;
    }

}
