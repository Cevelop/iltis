package ch.hsr.ifs.iltis.core.core.data;

import java.util.Optional;

import ch.hsr.ifs.iltis.core.core.functional.functions.Equals;


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

   /**
    * {@inheritDoc}
    * 
    * Used to print the content of an AbstractPair. The pair is always printed in this style (first,second) this is kept even for nested
    * structures like (first.first, first.second.first, first.second.second, second.first, second.second).
    * 
    */
   @Override
   public String toString() {
      return "(" + getFirstString() + ", " + getSecondString() + ")";
   }

   protected String getFirstString() {
      return first == null ? "null" : ((first instanceof AbstractPair ? ((AbstractPair<?, ?>) first).toRawString() : first.toString()));
   }

   protected String getSecondString() {
      return second == null ? "null" : ((second instanceof AbstractPair ? ((AbstractPair<?, ?>) second).toRawString() : second.toString()));
   }

   /**
    * An internal method used to generate the substring for nested pairs.
    * 
    * @return
    */
   protected String toRawString() {
      return getFirstString() + ", " + getSecondString();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (first == null ? 0 : first.hashCode());
      result = prime * result + (second == null ? 0 : second.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @SuppressWarnings("rawtypes")
   public boolean equals(final Object obj) {
      if (this == obj) { return true; }
      if (obj == null) { return false; }
      if (getClass() != obj.getClass()) { return false; }
      final AbstractPair other = (AbstractPair) obj;
      if (first == null && other.first != null) { return false; }
      if (!first.equals(other.first)) { return false; }
      if (second == null && other.second != null) { return false; }
      if (!second.equals(other.second)) { return false; }
      return true;
   }

   /**
    * Used to compare two AbstractPairs with a custom {@link Equals} comparator.
    * 
    * @param obj
    *        The object to which to compare {@code this}
    * @param comparator
    *        The comparator
    * @return The same value as the equal method of the comparator returns
    */
   public <SndPair> boolean equals(final SndPair obj, Equals<AbstractPair<?, ?>, SndPair> comparator) {
      return comparator.equal(this, obj);
   }

   /**
    * Used to access the elements in a comparison.
    * 
    * @author tstauber
    *
    * @param <Other>
    *        The type of the element to compare to this element
    */
   public static abstract class AbstractPairEquals<Other> implements Equals<AbstractPair<?, ?>, Other> {

      /**
       * If the passed object is an instance of AbstractPair, then the first element will be returned else null
       * 
       * @param obj
       *        The object
       * @return The first element if obj is an AbstractPair, else null
       */
      @SuppressWarnings("rawtypes")
      public Object accessFirst(Other obj) {
         if (obj instanceof AbstractPair) {
            return ((AbstractPair) obj).first;
         } else {
            return null;
         }
      }

      /**
       * If the passed object is an instance of AbstractPair, then the second element will be returned else null
       * 
       * @param obj
       *        The object
       * @return The second element if obj is an AbstractPair, else null
       */
      @SuppressWarnings("rawtypes")
      public Object accessSecond(Other obj) {
         if (obj instanceof AbstractPair) {
            return ((AbstractPair) obj).second;
         } else {
            return null;
         }
      }
   }

   /**
    * Used to test, if all elements in the pair are equal to each other. This works for nested constructs.
    * 
    * @param pair
    *        The abstractPair to test
    * @return {@code true} if all elements are equal.
    */
   @SuppressWarnings("unlikely-arg-type")
   public static <T1, T2> boolean allElementEquals(AbstractPair<T1, T2> pair) {
      return allElementEquals((l, r) -> l.equals(r), pair);
   }

   /**
    * Used to test, if NOT all elements in the pair are equal to each other. This works for nested constructs.
    * 
    * @param pair
    *        The abstractPair to test
    * @return {@code false} if all elements are equal.
    */
   public static <T1, T2> boolean notAllElementEquals(AbstractPair<T1, T2> pair) {
      return !allElementEquals(pair);
   }

   /**
    * Used to test, if all elements in the pair are equal to each other. This works for nested constructs. This method takes a custom comparator.
    * 
    * @param comparator
    *        The comparator which tests for the equality.
    * @param pair
    *        The abstractPair to test
    * 
    * @return {@code true} if all elements are equal according to the comparator.
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public static <T1, T2> boolean allElementEquals(Equals<T1, T2> comparator, AbstractPair<T1, T2> pair) {

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
    * @returns An empty Optional if all values are {@code null}
    * @returns {@code null} if not all values are equal
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

      if (f == null || s == null) { return null; }
      if (!f.isPresent() && !s.isPresent()) return f;
      if (f.isPresent() && s.isPresent() && f.get().getClass().isInstance(s.get()) && comparator.equal(f.get(), s.get())) {
         return f;
      } else {
         return null;
      }
   }
   
   /**
    * Helper class for easier nesting 
    * @author tstauber
    *
    * @param <ST1>
    * @param <ST2>
    */
   protected static class NestingHelper<ST1,ST2> extends AbstractPair<ST1,ST2>{

      public NestingHelper(ST1 first, ST2 second) {
         super(first, second);
      }
      
      public ST1 first() {
         return first;
      }
      
      public ST2 second() {
         return second;
      }
      
   }
}
