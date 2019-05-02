package ch.hsr.ifs.iltis.core.core.functional.functions;

import java.util.Comparator;
import java.util.function.Predicate;


/**
 * Provides the ability to compare two objects for equality. It is similar to a {@link Comparator}, but instead of {@code -1, 0, 1} it returns
 * {@code true} of {@code false}
 *
 * @author tstauber
 *
 * @param <T1>
 * The first object to compare
 * @param <T2>
 * The second object to compare
 */
@FunctionalInterface
public interface Equals<T1, T2> {

    /* Equality methods */
    
    /**
     * Canned method to test for reference equality
     * 
     * @param l
     * The first object
     * @param r
     * The second object
     * @return {@code true} iff l is the same object as r
     * 
     * @param <T1>
     * The first object to compare
     * @param <T2>
     * The second object to compare
     * 
     * @since 1.1
     */
    public static <T1, T2> boolean refEq(final T1 l, final T2 r) {
        return l == r;
    }


    /**
     * Creates a {@link Predicate} using {@link ==} to test the other values.
     * 
     * @param <T1>
     * The type of the left value.
     * @param <T2>
     * The type of the right value.
     * @param left
     * The fixed left value.
     * @return A {@link Predicate} returning {@code true} iff left is the same object as the value the predicate is called with.
     * 
     * @since 2.1
     */
    public static <T1, T2> Predicate<T2> genRefEq(final T1 left) {
        return (T2 right) -> left == right;
    }

    /**
     * Canned method to test if the result of {@link T1#toString()} and {@link T2#toString()} is equal. This test is {@code null} safe.
     * 
     * @param l
     * The first object
     * @param r
     * The second object
     * @return {@code true} iff either l and r are {@code null}, or if{@link T1#toString()}{@code .equals(}{@link T2#toString()}{@code )}
     * 
     * @param <T1>
     * The first object to compare
     * @param <T2>
     * The second object to compare
     * 
     * @since 1.1
     */
    public static <T1, T2> boolean toStrEq(final T1 l, final T2 r) {
        if (l == null && r == null) return true;
        if (l == null || r == null) return false;
        return l.toString().equals(r.toString());
    }

    /* Functional Interface */
    boolean equal(T1 l, T2 r);
}
