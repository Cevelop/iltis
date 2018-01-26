package ch.hsr.ifs.iltis.core.tests.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import ch.hsr.ifs.iltis.core.collections.Stack;


public class StackTest {

   private Stack<String> stack;

   @Before
   public void setUp() {
      stack = new Stack<>();
   }

   @Test
   public void newStackIsEmpty() {
      assertTrue(stack.isEmpty());
      assertEquals(0, stack.size());
   }

   @Test
   public void pushesToEmptyStack() {
      final int numberOfPushes = 6;

      for (int i = 0; i < numberOfPushes; i++) {
         stack.push("test");
      }

      assertFalse(stack.isEmpty());
      assertEquals(numberOfPushes, stack.size());
   }

   @Test
   public void pushThenPopYieldsSameElementAndEmptyStack() {
      final String message = "test";
      stack.push(message);
      assertEquals(message, stack.pop());
      assertTrue(stack.isEmpty());
   }

   @Test
   public void pushThenPeekYieldsSameElementWithoutRemovingIt() {
      final String message = "test";
      stack.push(message);
      final int size = stack.size();
      assertEquals(message, stack.peek());
      assertEquals(size, stack.size());
   }

   @Test
   public void popDownToEmptyStack() {
      final int numberOfPushes = (int) (Math.random() * 42 + 1);

      for (int i = 0; i < numberOfPushes; i++) {
         stack.push("test");
      }

      for (int i = 0; i < numberOfPushes; i++) {
         stack.pop();
      }
      assertTrue(stack.isEmpty());
      assertEquals(0, stack.size());
   }

   @Test(expected = NoSuchElementException.class)
   public void popOnEmptyStack() {
      assertTrue(stack.isEmpty());
      stack.pop();
   }

   @Test(expected = NoSuchElementException.class)
   public void peekIntoEmptyStack() {
      assertTrue(stack.isEmpty());
      stack.peek();
   }
}
