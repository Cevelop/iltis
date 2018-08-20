package ch.hsr.ifs.iltis.core.core.functional.functions;

/**
 * Like a {@linkplain Consumer}, but takes two parameters of different types
 * 
 * @author hpatzen
 * 
 * @see Consumer
 *
 * @param <P1>
 *        The type of the first argument
 * @param <P2>
 *        The type of the second argument
 */
@FunctionalInterface
public interface Consumer2<P1, P2> {

   default Consumer<P2> curry(P1 param1) {
      return new CurriedConsumer<P1, P2>(param1, this);
   }

   /**
    * Executes the Function.
    * 
    * @param param1
    *        The first argument
    * @param param2
    *        The second argument
    */
   public void accept(P1 param1, P2 param2);
}



class CurriedConsumer<P1, P2> implements Consumer<P2> {

   private P1                param1;
   private Consumer2<P1, P2> fun;

   protected CurriedConsumer(P1 param1, Consumer2<P1, P2> fun) {
      this.param1 = param1;
      this.fun = fun;
   }

   @Override
   public void accept(P2 param2) {
      fun.accept(param1, param2);
   }

}
