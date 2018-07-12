package ch.hsr.ifs.iltis.core.core.tests.functional

import java.util.ArrayList
import java.util.Vector
import java.util.stream.Collectors

import org.junit.Assert.assertEquals
import org.junit.Test

import ch.hsr.ifs.iltis.core.core.functional.StreamFactory


class StreamFactoryTest {

   @Test
   fun `stream() factory function produces valid stream from Enumeration`() {
	   
      val source = Vector<String>();
      source.add("foo");
      source.add("bar");
      source.add("baz");

      val expected = "foo-bar-baz";
      val actual = StreamFactory.stream(source.elements()).collect(Collectors.joining("-"));
      assertEquals(expected, actual);
   }

   @Test
   fun `stream() factory function produces valid stream from Iterator`() {
      val source =arrayListOf("foo", "bar", "baz")

      val expected = "foo-bar-baz";
      val actual = StreamFactory.stream(source.iterator()).collect(Collectors.joining("-"));
      assertEquals(expected, actual);
   }

}
