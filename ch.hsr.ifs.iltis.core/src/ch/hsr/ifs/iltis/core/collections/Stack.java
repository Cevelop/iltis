package ch.hsr.ifs.iltis.core.collections;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Stream;


/**
 * An implementation of a stack. As {@link java.util.Stack} is seriously flawed, this implementation uses {@link java.util.Deque}
 * as recommended in the documentation of {@link java.util.Stack}.
 * 
 * Taken from Mockator, and adjusted to support {@link Stream}s
 * 
 * @author tstauber
 *
 * @param <T> The type of the elements
 */
public class Stack<T> {

   private final Deque<T> stack;
  

   public Stack() {
      stack = new ArrayDeque<>();
   }

   /** 
    * Pushes a new element to the top of the stack
    */
   public void push(final T node) {
      stack.addFirst(node);
   }

   /**
    * Removes and returns the top element from the stack
    */
   public T pop() {
      return stack.removeFirst();
   }

   /**
    * Returns the top element from the stack without removing it
    */
   public T peek() {
      return stack.getFirst();
   }

   /**
    * Drops all elements from the stack
    */
   public void clear() {
      stack.clear();
   }

   /**
    * Returns {@code true} if size is 0
    */
   public boolean isEmpty() {
      return stack.isEmpty();
   }

   /**
    * Returns the size of the stack
    */
   public int size() {
      return stack.size();
   }
   
   /**
    * Returns a stream consisting of the elements of the Stack. This does not consume the elements of the Stack.
    */
   public Stream<T> nonConsumingStream(){
      return stack.stream();
   }

}
