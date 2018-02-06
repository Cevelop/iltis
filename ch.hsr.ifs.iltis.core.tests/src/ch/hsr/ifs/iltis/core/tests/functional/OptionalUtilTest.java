package ch.hsr.ifs.iltis.core.tests.functional;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import ch.hsr.ifs.iltis.core.data.Wrapper;
import ch.hsr.ifs.iltis.core.functional.OptionalUtil;


public class OptionalUtilTest {

   @Test
   public void returnIfPresentElseEmptySupT() {
      Optional<String> source = Optional.of("foo");
      Optional<String> actual = OptionalUtil.returnIfPresentElseEmpty(source, () -> Optional.of("bar"));
      Assert.assertEquals(Optional.of("bar"), actual);
   }

   @Test
   public void returnIfPresentElseEmptySupE() {
      Optional<String> source = Optional.empty();
      Optional<String> actual = OptionalUtil.returnIfPresentElseEmpty(source, () -> Optional.of("bar"));
      Assert.assertEquals(Optional.empty(), actual);
   }

   @Test
   public void returnIfPresentElseEmptyFunT() {
      Optional<String> source = Optional.of("foo");
      Optional<String> actual = OptionalUtil.returnIfPresentElseEmpty(source, (it) -> Optional.of(it + "bar"));
      Assert.assertEquals(Optional.of("foobar"), actual);
   }

   @Test
   public void returnIfPresentElseEmptyFunE() {
      Optional<String> source = Optional.empty();
      Optional<String> actual = OptionalUtil.returnIfPresentElseEmpty(source, (it) -> Optional.of(it + "bar"));
      Assert.assertEquals(Optional.empty(), actual);
   }

   @Test
   public void returnIfPresentElseNullFunT() {
      Optional<String> source = Optional.of("foo");
      String actual = OptionalUtil.returnIfPresentElseNull(source, (it) -> it + "bar");
      Assert.assertEquals("foobar", actual);
   }

   @Test
   public void returnIfPresentElseNullFunE() {
      Optional<String> source = Optional.empty();
      String actual = OptionalUtil.returnIfPresentElseNull(source, (it) -> it);
      Assert.assertEquals(null, actual);
   }

   @Test
   public void returnIfPresentElseSupSupT() {
      Optional<String> source = Optional.of("foo");
      String actual = OptionalUtil.returnIfPresentElse(source, () -> "bar", () -> "baz");
      Assert.assertEquals("bar", actual);
   }

   @Test
   public void returnIfPresentElseSupSupE() {
      Optional<String> source = Optional.empty();
      String actual = OptionalUtil.returnIfPresentElse(source, () -> "bar", () -> "baz");
      Assert.assertEquals("baz", actual);
   }

   @Test
   public void returnIfPresentElseFunSupT() {
      Optional<String> source = Optional.of("foo");
      String actual = OptionalUtil.returnIfPresentElse(source, (it) -> it + "bar", () -> "baz");
      Assert.assertEquals("foobar", actual);
   }

   @Test
   public void returnIfPresentElseFunSupE() {
      Optional<String> source = Optional.empty();
      String actual = OptionalUtil.returnIfPresentElse(source, (it) -> it + "bar", () -> "baz");
      Assert.assertEquals("baz", actual);
   }

   @Test
   public void returnIfPresentElseFunValT() {
      Optional<String> source = Optional.of("foo");
      String actual = OptionalUtil.returnIfPresentElse(source, (it) -> it + "bar", "baz");
      Assert.assertEquals("foobar", actual);
   }

   @Test
   public void returnIfPresentElseFunValE() {
      Optional<String> source = Optional.empty();
      String actual = OptionalUtil.returnIfPresentElse(source, (it) -> it + "bar", "baz");
      Assert.assertEquals("baz", actual);
   }

   @Test(expected = Exception.class)
   public void returnIfPresentTElseFunSupT() throws Exception {
      Optional<String> source = Optional.of("foo");
      OptionalUtil.returnIfPresentTElse(source, (it) -> {
         throw new Exception();
      }, () -> "baz");
   }

   @Test
   public void returnIfPresentTElseFunSupE() throws Exception {
      Optional<String> source = Optional.empty();
      String actual = OptionalUtil.returnIfPresentTElse(source, (it) -> {
         throw new Exception();
      }, () -> "baz");
      Assert.assertEquals("baz", actual);
   }

   @Test
   public void returnIfPresentElseTFunSupT() throws Exception {
      Optional<String> source = Optional.of("foo");
      String actual = OptionalUtil.returnIfPresentElseT(source, (it) -> it + "bar", () -> {
         throw new Exception();
      });
      Assert.assertEquals("foobar", actual);
   }

   @Test(expected = Exception.class)
   public void returnIfPresentElseTFunSupE() throws Exception {
      Optional<String> source = Optional.empty();
      OptionalUtil.returnIfPresentElseT(source, (it) -> it + "bar", () -> {
         throw new Exception();
      });
   }

   @Test(expected = Exception.class)
   public void returnIfPresentTElseTFunSupT() throws Exception {
      Optional<String> source = Optional.of("foo");
      OptionalUtil.returnIfPresentTElseT(source, (it) -> {
         throw new Exception();
      }, () -> {
         throw new Exception();
      });
   }

   @Test(expected = Exception.class)
   public void returnIfPresentTElseTFunSupE() throws Exception {
      Optional<String> source = Optional.empty();
      OptionalUtil.returnIfPresentTElseT(source, (it) -> {
         throw new Exception();
      }, () -> {
         throw new Exception();
      });
   }

   @Test
   public void doIfPresentT() {
      Optional<String> source = Optional.of("foo");
      Wrapper<String> val = new Wrapper<>("old");
      OptionalUtil.doIfPresent(source, (it) -> val.wrapped = it + "bar");
      Assert.assertEquals("foobar", val.wrapped);
   }

   @Test
   public void doIfPresentE() {
      Optional<String> source = Optional.empty();
      Wrapper<String> val = new Wrapper<>("foo");
      OptionalUtil.doIfPresent(source, (it) -> val.wrapped = it + "bar");
      Assert.assertEquals("foo", val.wrapped);
   }

   @Test
   public void doIfPresentElseT() {
      Optional<String> source = Optional.of("foo");
      Wrapper<String> val = new Wrapper<>("old");
      OptionalUtil.doIfPresentElse(source, (it) -> val.wrapped = it + "bar", () -> val.wrapped += "baz");
      Assert.assertEquals("foobar", val.wrapped);
   }

   @Test
   public void doIfPresentElseE() {
      Optional<String> source = Optional.empty();
      Wrapper<String> val = new Wrapper<>("foo");
      OptionalUtil.doIfPresentElse(source, (it) -> val.wrapped = it + "bar", () -> val.wrapped += "baz");
      Assert.assertEquals("foobaz", val.wrapped);
   }

   @Test(expected = Exception.class)
   public void doIfPresentTT() throws Exception {
      Optional<String> source = Optional.of("foo");
      OptionalUtil.doIfPresentT(source, (it) -> {
         throw new Exception();
      });
   }

   @Test
   public void doIfPresentTE() throws Exception {
      Optional<String> source = Optional.empty();
      Wrapper<String> val = new Wrapper<>("old");
      OptionalUtil.doIfPresentT(source, (it) -> {
         throw new Exception();
      });
      Assert.assertEquals("old", val.wrapped);
   }

   @Test(expected = Exception.class)
   public void doIfPresentElseTT() throws Exception {
      Optional<String> source = Optional.of("foo");
      Wrapper<String> val = new Wrapper<>("old");
      OptionalUtil.doIfPresentTElse(source, (it) -> {
         throw new Exception();
      }, () -> val.wrapped += "foo");
   }

   @Test
   public void doIfPresentElseTE() throws Exception {
      Optional<String> source = Optional.empty();
      Wrapper<String> val = new Wrapper<>("old");
      OptionalUtil.doIfPresentTElse(source, (it) -> {
         throw new Exception();
      }, () -> val.wrapped += "foo");
      Assert.assertEquals("oldfoo", val.wrapped);
   }

}
