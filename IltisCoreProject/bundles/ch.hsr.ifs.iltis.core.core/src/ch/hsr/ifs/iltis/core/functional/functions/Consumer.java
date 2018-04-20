package ch.hsr.ifs.iltis.core.functional.functions;

/**
 * Like a {@linkplain Consumer}, but curryable
 * 
 * @author tstauber
 * 
 * @see Consumer
 *
 * @param <P>
 *        The type of the argument
 */
@FunctionalInterface
public interface Consumer<T> extends java.util.function.Consumer<T> {

   default Runnable curry(T param) {
      return new CurriedRunnable<T>(param, this);
   }

}



class CurriedRunnable<T> implements Runnable {

   private T           param;
   private Consumer<T> fun;

   protected CurriedRunnable(T param, Consumer<T> fun) {
      this.param = param;
      this.fun = fun;
   }

   @Override
   public void run() {
      fun.accept(param);
   }
}
