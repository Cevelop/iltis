package ch.hsr.ifs.iltis.core.core.functional.functions;

/**
 * Like a {@linkplain java.util.function.Consumer}, but curryable
 *
 * @author tstauber
 *
 * @see Consumer
 *
 * @param <T>
 * The type of the argument
 */
@FunctionalInterface
public interface Consumer<T> extends java.util.function.Consumer<T> {

    default Runnable curry(final T param) {
        return new CurriedRunnable<>(param, this);
    }

}



class CurriedRunnable<T> implements Runnable {

    private final T           param;
    private final Consumer<T> fun;

    protected CurriedRunnable(final T param, final Consumer<T> fun) {
        this.param = param;
        this.fun = fun;
    }

    @Override
    public void run() {
        fun.accept(param);
    }
}
