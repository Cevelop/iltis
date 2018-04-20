package ch.hsr.ifs.iltis.core.core.functional.functions;

import java.util.function.Supplier;


/**
 * A Supplier which is able to throw an exception.
 * 
 * @see Supplier
 * @author tstauber
 *
 * @param <R>
 *        The return type
 * @param <E>
 *        The exception type
 */
@FunctionalInterface
public interface ThrowingSupplier<R, E extends Throwable> {

   /**
    * Executes the function.
    * 
    * @return A value of type {@link R}
    * @throws E
    */
   public R get() throws E;
}
