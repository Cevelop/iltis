package ch.hsr.ifs.iltis.core.core.functional.functions;

import java.util.function.Function;


/**
 * Like a {@linkplain Function}, but takes three parameters of different types
 * 
 * @author tstauber
 * 
 * @see Function
 *
 * @param <P1>
 *        The type of the first argument
 * @param <P2>
 *        The type of the second argument
 * @param <P3>
 *        The type of the third argument
 * @param <R>
 *        The return type
 */
@FunctionalInterface
public interface Function3<P1, P2, P3, R> {

   default Function2<P2, P3, R> curry(P1 param1) {
      return new CurriedFunction2<P1, P2, P3, R>(param1, this);
   }

   /**
    * Executes the Function.
    * 
    * @param param1
    *        The first argument
    * @param param2
    *        The second argument
    * @param param3
    *        The third argument
    * @return A value of {@link R}
    */
   public R apply(P1 param1, P2 param2, P3 param3);
}



class CurriedFunction2<P1, P2, P3, R> implements Function2<P2, P3, R> {

   private P1                       param1;
   private Function3<P1, P2, P3, R> fun;

   protected CurriedFunction2(P1 param1, Function3<P1, P2, P3, R> fun) {
      this.param1 = param1;
      this.fun = fun;
   }

   @Override
   public R apply(P2 param2, P3 param3) {
      return fun.apply(param1, param2, param3);
   }

}
