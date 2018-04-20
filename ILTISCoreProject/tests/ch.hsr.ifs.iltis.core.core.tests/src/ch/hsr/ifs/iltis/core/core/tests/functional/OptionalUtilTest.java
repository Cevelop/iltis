package ch.hsr.ifs.iltis.core.core.tests.functional;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import ch.hsr.ifs.iltis.core.core.data.Wrapper;
import ch.hsr.ifs.iltis.core.core.functional.OptionalUtil;


public class OptionalUtilTest {

   @Test
   public void doIfPresentElseT() {
      Optional<String> source = Optional.of("foo");
      Wrapper<String> val = new Wrapper<>("old");
      OptionalUtil.of(source).ifPresent(it -> val.wrapped = it + "bar").ifNotPresent(() -> val.wrapped += "baz");
      Assert.assertEquals("foobar", val.wrapped);
   }

   @Test
   public void doIfPresentElseE() {
      Optional<String> source = Optional.empty();
      Wrapper<String> val = new Wrapper<>("foo");
      OptionalUtil.of(source).ifPresent(it -> val.wrapped = it + "bar").ifNotPresent(() -> val.wrapped += "baz");
      Assert.assertEquals("foobaz", val.wrapped);
   }

   @Test(expected = Exception.class)
   public void doIfPresentTT() throws Exception {
      Optional<String> source = Optional.of("foo");
      OptionalUtil.of(source).ifPresentT(it -> {
         throw new Exception();
      });
   }

   @Test(expected = Exception.class)
   public void doIfPresentTE() throws Exception {
      Optional<String> source = Optional.empty();
      Wrapper<String> val = new Wrapper<>("old");
      OptionalUtil.of(source).ifNotPresentT((it) -> {
         throw new Exception();
      });
      Assert.assertEquals("old", val.wrapped);
   }

   @Test
   public void doIfPresentElseTT() throws Exception {
      Optional<String> source = Optional.of("foo");
      OptionalUtil.of(source).ifNotPresentT((it) -> {
         throw new Exception();
      });
   }
}
