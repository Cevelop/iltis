package ch.hsr.ifs.iltis.core.functional.functions;

import java.util.function.Supplier;


/**
 * Like a {@linkplain Function}, but curryable
 * 
 * @author tstauber
 * 
 * @see Function
 *
 * @param <P>
 *        The type of the argument
 * @param <R>
 *        The return type
 */
@FunctionalInterface
public interface Function<T, R> extends java.util.function.Function<T, R> {

   default Supplier<R> curry(T param) {
      return new CurriedSupplier<T, R>(param, this);
   }

}



class CurriedSupplier<T, R> implements Supplier<R> {

   private T              param;
   private Function<T, R> fun;

   protected CurriedSupplier(T param, Function<T, R> fun) {
      this.param = param;
      this.fun = fun;
   }

   @Override
   public R get() {
      return fun.apply(param);
   }
}
