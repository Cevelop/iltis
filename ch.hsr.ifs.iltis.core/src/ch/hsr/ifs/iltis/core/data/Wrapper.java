package ch.hsr.ifs.iltis.core.data;

public class Wrapper<T> {

   public T wrapped;

   public Wrapper(final T target) {
      wrapped = target;
   }
}
