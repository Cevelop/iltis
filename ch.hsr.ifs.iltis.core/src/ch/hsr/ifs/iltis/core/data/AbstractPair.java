package ch.hsr.ifs.iltis.core.data;

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
      str.append("( ");
      str.append((first instanceof AbstractPair) ? ((AbstractPair) first).toRawString() : first.toString());
      str.append((second instanceof AbstractPair) ? ((AbstractPair) second).toRawString() : second.toString());
      return str.append(" )").toString();
   }

   @SuppressWarnings("rawtypes")
   protected String toRawString() {
      final StringBuilder str = new StringBuilder();
      str.append((first instanceof AbstractPair) ? ((AbstractPair) first).toRawString() : first.toString());
      str.append((second instanceof AbstractPair) ? ((AbstractPair) second).toRawString() : second.toString());
      return str.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((first == null) ? 0 : first.hashCode());
      result = prime * result + ((second == null) ? 0 : second.hashCode());
      return result;
   }

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

}
