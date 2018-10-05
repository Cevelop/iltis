package ch.hsr.ifs.iltis.core.core.functional.functions;

/**
 * Like a {@linkplain Function}, but takes two parameters of different types
 *
 * @author tstauber
 *
 * @see Function
 *
 * @param <P1>
 * The type of the first argument
 * @param <P2>
 * The type of the second argument
 * @param <R>
 * The return type
 */
@FunctionalInterface
public interface Function2<P1, P2, R> {

    /**
     * 
     * Fills in the first parameter of this function.
     * 
     * @param param1
     * The value to bind the first parameter to.
     * @return A {@link CurriedFunction} with param1 filled in.
     * 
     * @since 1.1
     */
    default Function<P2, R> curry(final P1 param1) {
        return new CurriedFunction<>(param1, this);
    }

    /**
     * Executes the Function.
     *
     * @param param1
     * The first argument
     * @param param2
     * The second argument
     * @return A value of {@link R}
     */
    public R apply(P1 param1, P2 param2);
}



class CurriedFunction<P1, P2, R> implements Function<P2, R> {

    private final P1                   param1;
    private final Function2<P1, P2, R> fun;

    protected CurriedFunction(final P1 param1, final Function2<P1, P2, R> fun) {
        this.param1 = param1;
        this.fun = fun;
    }

    @Override
    public R apply(final P2 param2) {
        return fun.apply(param1, param2);
    }

}
