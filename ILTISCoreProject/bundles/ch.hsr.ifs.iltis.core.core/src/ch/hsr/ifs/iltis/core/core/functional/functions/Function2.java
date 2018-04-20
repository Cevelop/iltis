package ch.hsr.ifs.iltis.core.core.functional.functions;

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

   default Function<P2, R> curry(P1 param1) {
      return new CurriedFunction<P1, P2, R>(param1, this);
   }

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



class CurriedFunction<P1, P2, R> implements Function<P2, R> {

   private P1                   param1;
   private Function2<P1, P2, R> fun;

   protected CurriedFunction(P1 param1, Function2<P1, P2, R> fun) {
      this.param1 = param1;
      this.fun = fun;
   }

   @Override
   public R apply(P2 param2) {
      return fun.apply(param1, param2);
   }

}
