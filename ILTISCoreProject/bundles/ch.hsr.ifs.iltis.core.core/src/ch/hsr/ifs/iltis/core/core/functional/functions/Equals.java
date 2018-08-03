package ch.hsr.ifs.iltis.core.core.functional.functions;

import java.util.Comparator;


/**
 * Provides the ability to compare two objects for equality. It is similar to a {@link Comparator}, but instead of {@code -1, 0, 1} it returns
 * {@code true} of {@code false}
 * 
 * @author tstauber
 *
 * @param <T1>
 *        The first object to compare
 * @param <T2>
 *        The second object to compare
 */
@FunctionalInterface
public interface Equals<T1, T2> {

   /* Equality methods */
   public static <T1,T2> boolean refEq(T1 l, T2 r) {
      return l == r;
   }

   public static <T1,T2> boolean toStrEq(T1 l, T2 r) {
      if (l == null && r == null) return true;
      if (l == null || r == null) return false;
      return l.toString().equals(r.toString());
   }

   /* Functional Interface */
   boolean equal(T1 l, T2 r);
}
