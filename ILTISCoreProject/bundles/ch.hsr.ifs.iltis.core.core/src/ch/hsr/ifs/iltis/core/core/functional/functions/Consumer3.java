package ch.hsr.ifs.iltis.core.core.functional.functions;

/**
 * Like a {@linkplain Consumer}, but takes three parameters of different types
 * 
 * @author hpatzen
 * 
 * @see Consumer
 *
 * @param <P1>
 *        The type of the first argument
 * @param <P2>
 *        The type of the second argument
 * @param <P3>
 *        The type of the third argument
 */
@FunctionalInterface
public interface Consumer3<P1, P2, P3> {

   default Consumer2<P2, P3> curry(P1 param1) {
      return new CurriedConsumer2<P1, P2, P3>(param1, this);
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
    */
   public void accept(P1 param1, P2 param2, P3 param3);
}



class CurriedConsumer2<P1, P2, P3> implements Consumer2<P2, P3> {

   private P1                param1;
   private Consumer3<P1, P2, P3> fun;

   protected CurriedConsumer2(P1 param1, Consumer3<P1, P2, P3> fun) {
      this.param1 = param1;
      this.fun = fun;
   }

   @Override
   public void accept(P2 param2, P3 param3) {
      fun.accept(param1, param2, param3);
   }

}
