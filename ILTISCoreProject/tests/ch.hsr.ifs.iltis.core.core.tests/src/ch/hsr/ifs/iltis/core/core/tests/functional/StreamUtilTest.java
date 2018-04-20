package ch.hsr.ifs.iltis.core.core.tests.functional;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ch.hsr.ifs.iltis.core.core.functional.Functional;
import ch.hsr.ifs.iltis.core.core.functional.StreamUtil;


public class StreamUtilTest {

   @Test
   public void toMapCollector() {
      String[] as = { "foo", "bar", "baz" };
      Integer[] bs = { 1, 2, 3, 4 };

      Map<String, Integer> actual = Functional.zip(as, bs).collect(StreamUtil.toMap());

      Map<String, Integer> expected = new HashMap<>();
      expected.put(as[0], bs[0]);
      expected.put(as[1], bs[1]);
      expected.put(as[2], bs[2]);
      expected.put(null, bs[3]);

      Assert.assertEquals(expected, actual);
   }

}
