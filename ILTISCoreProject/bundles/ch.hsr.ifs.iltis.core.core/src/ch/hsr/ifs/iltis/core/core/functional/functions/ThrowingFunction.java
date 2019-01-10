package ch.hsr.ifs.iltis.core.core.functional.functions;

import java.util.function.Function;


/**
 * A Function which is able to throw an exception.
 *
 * @see Function
 * @author tstauber
 *
 * @param <T>
 * The parameter type
 * @param <R>
 * The return type
 * @param <E>
 * The exception type
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {

    /**
     * Executes the Function
     *
     * @param t
     * The argument
     * @return A value of type {@link R}
     * @throws E
     * The thrown exception
     */
    public R apply(T t) throws E;
}
