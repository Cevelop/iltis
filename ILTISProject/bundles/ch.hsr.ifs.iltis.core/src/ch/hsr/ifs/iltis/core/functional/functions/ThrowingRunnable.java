package ch.hsr.ifs.iltis.core.functional.functions;

/**
 * A Runnable which is able to throw an exception.
 *
 * @see Runnable
 * @author tstauber
 *
 * @param <E>
 * The exception type
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {

    /**
     * Executes the runnable
     *
     * @throws E
     * The thrown exception
     */
    public void run() throws E;
}
