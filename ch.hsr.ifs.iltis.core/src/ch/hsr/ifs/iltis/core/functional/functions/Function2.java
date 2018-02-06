package ch.hsr.ifs.iltis.core.functional.functions;

import java.util.function.Function;


/**
 * Like a {@linkplain Function}, but takes two parameters of different types
 * 
 * @author tstauber
 * 
 * @see Function
 *
 * @param <P1>
 *        The type of the first argument
 * @param <P2>
 *        The type of the second argument
 * @param <R>
 *        The return type
 */
@FunctionalInterface
public interface Function2<P1, P2, R> {

   /**
    * Executes the Function.
    * 
    * @param param1
    *        The first argument
    * @param param2
    *        The second argument
    * @return A value of {@link R}
    */
   public R apply(P1 param1, P2 param2);
}
