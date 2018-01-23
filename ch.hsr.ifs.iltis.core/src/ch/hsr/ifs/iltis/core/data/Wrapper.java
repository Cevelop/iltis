package ch.hsr.ifs.iltis.core.data;

/**
 * A simple wrapper.
 *
 * @author tstauber
 */
public class Wrapper<T> {

   public T wrapped;

   public Wrapper(final T target) {
      wrapped = target;
   }
}
