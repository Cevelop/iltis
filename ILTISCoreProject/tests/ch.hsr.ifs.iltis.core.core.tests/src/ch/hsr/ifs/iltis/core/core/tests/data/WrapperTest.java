package ch.hsr.ifs.iltis.core.core.tests.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.core.data.Wrapper;


public class WrapperTest {

   @Test
   public void lambdaEscape() {
      final Wrapper<String> w = new Wrapper<>("");

      final String[] strings = { "foo", "bar", "baz", "foobar", "foobaz" };
      Arrays.stream(strings).forEach(s -> {
         if (s.contains("foo")) {
            w.wrapped += s;
         }
      });

      assertEquals("foofoobarfoobaz", w.wrapped);
   }

}