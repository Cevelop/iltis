package ch.hsr.ifs.iltis.core.tests.functional;

import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import ch.hsr.ifs.iltis.core.functional.StreamFactory;


public class StreamFactoryTest {

   @Test
   public void fromEnumerationTest() {
      Vector<String> source = new Vector<>();
      source.add("foo"); source.add("bar"); source.add("baz");
      
      String expected = "foo-bar-baz";
      String actual = StreamFactory.stream(source.elements()).collect(Collectors.joining("-"));
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void fromIteratorTestWhichAlsoTestsSpliterator() {
      ArrayList<String> source = new ArrayList<>();
      source.add("foo"); source.add("bar"); source.add("baz");
      
      String expected = "foo-bar-baz";
      String actual = StreamFactory.stream(source.iterator()).collect(Collectors.joining("-"));
      Assert.assertEquals(expected, actual);
   }

}
