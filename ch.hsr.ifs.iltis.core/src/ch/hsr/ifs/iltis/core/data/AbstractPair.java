package ch.hsr.ifs.iltis.core.data;

import java.util.Optional;

import ch.hsr.ifs.iltis.core.functional.functions.Equals;


/**
 * A templated abstract implementation of a Pair
 *
 * Can be chained like {@code Tripple<T1, T2, T3> extends AbstractPair<T1, AbstractPair<T2, T3>>}.
 *
 * @author tstauber *
 *
 * @param <T1>
 *        Template-type of first element
 * @param <T2>
 *        Template-type of second element
 */
public abstract class AbstractPair<T1, T2> {

   protected T1 first;
   protected T2 second;

   public AbstractPair(final T1 first, final T2 second) {
      this.first = first;
      this.second = second;
   }

   @SuppressWarnings("rawtypes")
   @Override
   public String toString() {
      final StringBuilder str = new StringBuilder();
      str.append("(");
      str.append(first instanceof AbstractPair ? ((AbstractPair) first).toRawString() : first.toString());
      str.append(", ");
      str.append(second instanceof AbstractPair ? ((AbstractPair) second).toRawString() : second.toString());
      return str.append(")").toString();
   }

   @SuppressWarnings("rawtypes")
   protected String toRawString() {
      final StringBuilder str = new StringBuilder();
      str.append(first instanceof AbstractPair ? ((AbstractPair) first).toRawString() : first.toString());
      str.append(", ");
      str.append(second instanceof AbstractPair ? ((AbstractPair) second).toRawString() : second.toString());
      return str.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (first == null ? 0 : first.hashCode());
      result = prime * result + (second == null ? 0 : second.hashCode());
      return result;
   }

   @Override
   @SuppressWarnings("rawtypes")
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final AbstractPair other = (AbstractPair) obj;
      if (first == null && other.first != null) {
         return false;
      }
      if (!first.equals(other.first)) {
         return false;
      }
      if (second == null && other.second != null) {
         return false;
      }
      if (!second.equals(other.second)) {
         return false;
      }
      return true;
   }

   public static <T1, T2> boolean allElementEquals(AbstractPair<T1, T2> pair) {
      return allElementEquals(pair, (l, r) -> l.equals(r));
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static <T1, T2> boolean allElementEquals(AbstractPair<T1, T2> pair, Equals<T1,T2> comparator) {

      Optional<T1> fst = pair.first instanceof AbstractPair ? ((AbstractPair) pair.first).getValueIfAllValuesSame(comparator) : Optional.ofNullable(
               pair.first);
      Optional<T2> snd = pair.second instanceof AbstractPair ? ((AbstractPair) pair.second).getValueIfAllValuesSame(comparator) : Optional.ofNullable(
               pair.second);

      if (fst == null || snd == null) return false;
      if (!fst.isPresent() && !snd.isPresent()) return true; //All elements are null
      return fst.isPresent() && snd.isPresent() && fst.get().getClass().isInstance(snd.get()) && comparator.equal(fst.get(), snd.get());
   }

   /**
    * @returns An Optional containing the value if all values are equal
    * @return An empty Optional if all values are null
    * @return null if not all values are equal
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   protected Optional<T1> getValueIfAllValuesSame(Equals<T1, T2> comparator) {
      Optional<T1> f = first == null ? Optional.empty() : Optional.of(first);
      Optional<T2> s = second == null ? Optional.empty() : Optional.of(second);
      if (first instanceof AbstractPair) {
         f = ((AbstractPair) first).getValueIfAllValuesSame(comparator);
      }
      if (second instanceof AbstractPair) {
         s = ((AbstractPair) second).getValueIfAllValuesSame(comparator);
      }

      if (f == null || s == null) {
         return null;
      }
      if (!f.isPresent() && !s.isPresent()) return f;
      if (f.isPresent() && s.isPresent() && f.get().getClass().isInstance(s.get()) && comparator.equal(f.get(), s.get())) {
         return f;
      } else {
         return null;
      }
   }
}
