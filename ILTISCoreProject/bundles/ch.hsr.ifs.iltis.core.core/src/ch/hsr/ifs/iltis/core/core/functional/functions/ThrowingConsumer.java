package ch.hsr.ifs.iltis.core.core.functional.functions;

import java.util.function.Consumer;


/**
 * A Consumer which is able to throw an exception.
 * 
 * @see Consumer
 * @author tstauber
 *
 * @param <T>
 *        The parameter type
 * @param <E>
 *        The type of the {@linkplain Exception} which could be thrown
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

   /**
    * Executes the consumer
    * 
    * @param t
    *        The argument
    * @throws E
    *         The thrown exception
    */
   public void accept(T t) throws E;
}
