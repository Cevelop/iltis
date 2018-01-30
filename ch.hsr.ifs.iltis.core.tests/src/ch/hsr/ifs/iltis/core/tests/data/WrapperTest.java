package ch.hsr.ifs.iltis.core.tests.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Test;
import ch.hsr.ifs.iltis.core.data.Wrapper;


public class WrapperTest {

   @Test
   public void lambdaEscape() {
      final Wrapper<String> w = new Wrapper<>("");

      final String[] strings = { "foo", "bar", "baz", "foobar", "foobaz" };
      Arrays.stream(strings, 0, 5).forEach(s -> {
         if (s.contains("foo")) {
            w.wrapped += s;
         }
      });

      assertEquals("foofoobarfoobaz", w.wrapped);
   }

}
