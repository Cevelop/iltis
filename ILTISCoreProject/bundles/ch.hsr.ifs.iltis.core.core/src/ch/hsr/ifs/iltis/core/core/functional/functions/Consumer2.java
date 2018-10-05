package ch.hsr.ifs.iltis.core.core.functional.functions;

/**
 * Like a {@linkplain Consumer}, but takes two parameters of different types
 *
 * @author hpatzen
 *
 * @see Consumer
 *
 * @param <P1>
 * The type of the first argument
 * @param <P2>
 * The type of the second argument
 * 
 * @since 1.1
 */
@FunctionalInterface
public interface Consumer2<P1, P2> {

    default Consumer<P2> curry(final P1 param1) {
        return new CurriedConsumer<>(param1, this);
    }

    /**
     * Executes the Function.
     *
     * @param param1
     * The first argument
     * @param param2
     * The second argument
     */
    public void accept(P1 param1, P2 param2);
}



class CurriedConsumer<P1, P2> implements Consumer<P2> {

    private final P1                param1;
    private final Consumer2<P1, P2> fun;

    protected CurriedConsumer(final P1 param1, final Consumer2<P1, P2> fun) {
        this.param1 = param1;
        this.fun = fun;
    }

    @Override
    public void accept(final P2 param2) {
        fun.accept(param1, param2);
    }

}
