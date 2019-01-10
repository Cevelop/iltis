package ch.hsr.ifs.iltis.core.core.functional.functions;

import java.util.function.Supplier;


/**
 * Like a {@linkplain java.util.function.Function}, but curryable
 *
 * @author tstauber
 *
 * @see Function
 *
 * @param <T>
 * The type of the argument
 * @param <R>
 * The return type
 */
@FunctionalInterface
public interface Function<T, R> extends java.util.function.Function<T, R> {

    default Supplier<R> curry(final T param) {
        return new CurriedSupplier<>(param, this);
    }

}



class CurriedSupplier<T, R> implements Supplier<R> {

    private final T              param;
    private final Function<T, R> fun;

    protected CurriedSupplier(final T param, final Function<T, R> fun) {
        this.param = param;
        this.fun = fun;
    }

    @Override
    public R get() {
        return fun.apply(param);
    }
}
