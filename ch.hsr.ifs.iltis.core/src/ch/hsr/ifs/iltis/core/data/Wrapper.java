package ch.hsr.ifs.iltis.core.data;

import java.util.Arrays;


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

   public void alter() {
      Wrapper<String> w = new Wrapper<>("");

      String[] strings = { "foo", "bar", "baz", "foobar", "foobaz" };
      Arrays.stream(strings, 0, 4).forEach(s -> {
         if (s.contains("foo")) {
            w.wrapped += s;
         }
      });
   }

}
