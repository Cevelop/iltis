package ch.hsr.ifs.iltis.core.core.exception;

import java.util.function.Supplier;

import ch.hsr.ifs.iltis.core.core.functional.functions.Consumer;
import ch.hsr.ifs.iltis.core.core.functional.functions.Function;
import ch.hsr.ifs.iltis.core.core.functional.functions.ThrowingConsumer;
import ch.hsr.ifs.iltis.core.core.functional.functions.ThrowingFunction;
import ch.hsr.ifs.iltis.core.core.functional.functions.ThrowingRunnable;
import ch.hsr.ifs.iltis.core.core.functional.functions.ThrowingSupplier;


/**
 * A specialized exception which can wrap other exceptions and rethrow them or itself as an unchecked exception.
 *
 * @author tstauber
 *
 */
public class ILTISException extends RuntimeException {

    public static ExceptionFactory Unless           = new ExceptionFactory(ILTISException::new);
    protected static final long    serialVersionUID = 0x1000000;
    private Exception              originalException;

    /**
     * Changes lambda to non-throwing
     *
     * @param <R>
     * The return type of the returned {@code Supplier}
     * @param <E>
     * The exception type extending {@code Exception}
     * @param functionalInterface
     * The lambda
     * @return The same lambda but all exceptions are thrown unchecked
     */
    public static <R, E extends Exception> Supplier<R> sterilize(final ThrowingSupplier<R, E> functionalInterface) {
        return () -> {
            try {
                return functionalInterface.get();
            } catch (final Exception e) {
                throw ILTISException.wrap(e).rethrowUnchecked();
            }
        };
    }

    /**
     * Changes lambda to non-throwing
     *
     * @param <R>
     * The return type of the returned {@code Consumer}
     * @param <E>
     * The exception type extending {@code Exception}
     * @param functionalInterface
     * The lambda
     * @return The same lambda but all exceptions are thrown unchecked
     */
    public static <R, E extends Exception> Consumer<R> sterilize(final ThrowingConsumer<R, E> functionalInterface) {
        return (r) -> {
            try {
                functionalInterface.accept(r);
            } catch (final Exception e) {
                throw ILTISException.wrap(e).rethrowUnchecked();
            }
        };
    }

    /**
     * Changes lambda to non-throwing
     *
     * @param <E>
     * The exception type extending {@code Exception}
     * @param functionalInterface
     * The lambda
     * @return The same lambda but all exceptions are thrown unchecked
     */
    public static <E extends Exception> Runnable sterilize(final ThrowingRunnable<E> functionalInterface) {
        return () -> {
            try {
                functionalInterface.run();
            } catch (final Exception ex) {
                throw ILTISException.wrap(ex).rethrowUnchecked();
            }
        };
    }

    /**
     * Changes lambda to non-throwing
     *
     * @param <T>
     * The input type of the returned {@code Function}
     * @param <R>
     * The return type of the returned {@code Function}
     * @param <E>
     * The exception type extending {@code Exception}
     * @param functionalInterface
     * The lambda
     * @return The same lambda but all exceptions are thrown unchecked
     */
    public static <T, R, E extends Exception> Function<T, R> sterilize(final ThrowingFunction<T, R, E> functionalInterface) {
        return (t) -> {
            try {
                return functionalInterface.apply(t);
            } catch (final Exception e) {
                throw ILTISException.wrap(e).rethrowUnchecked();
            }
        };
    }

    /**
     * Creates a new {@code ILTISException} containing a message
     *
     * @param message
     * The message displayed if the exception is thrown
     */
    public ILTISException(final String message) {
        this(message, true);
    }

    /**
     * Creates a new {@code ILTISException} containing a message but without a stack-trace if createStacktrace is false
     *
     * @param message
     * The message displayed if the exception is thrown
     * @param createStacktrace
     * Whether to create a stack-trace
     */
    protected ILTISException(final String message, final boolean createStacktrace) {
        super(message, null, true, createStacktrace);
    }

    /**
     * Creates a new {@code ILTISException} wrapping another exception
     *
     * @param originalException
     * The original exception which will be wrapped
     */
    public ILTISException(final Exception originalException) {
        super(originalException);
        this.originalException = originalException;
    }

    public static ILTISException wrap(final Exception originalException) {
        return new ILTISException(originalException);
    }

    /**
     * Creates a new {@code ILTISException} with a message and a cause
     *
     * @param message
     * The message displayed if the exception is thrown
     * @param cause
     * The cause why this exception is thrown (follow up exceptions). {@code null} means the reason is unknown or non existent.
     */
    public ILTISException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@code ILTISException}
     *
     * @param message
     * The message displayed if the exception is thrown
     * @param cause
     * The cause why this exception is thrown (follow up exceptions). {@code null} means the reason is unknown or non existent.
     * @param enableSuppression
     * Whether or not suppression is enabled
     * or disabled
     * @param writableStackTrace
     * Whether or not the stack-trace should be writable
     */
    public ILTISException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * If the {@code ILTISException} was created with a wrapped exception, this throws the wrapped exception, otherwise it throws itself.
     *
     * @throws Exception
     * A re-throw of the original {@code Exception} or {@code this}
     */
    public void rethrow() throws Exception {
        if (originalException != null) {
            throw originalException;
        } else {
            throw this;
        }
    }

    /**
     * Rethrows the wrapped exception as an unchecked exception. If there is no wrapped exception, the ILTISException rethrows itself as an unchecked
     * exception.
     *
     * @return Theoretically a {@code RuntimeException} but that never happens, as an unchecked Exception is thrown in this method.
     */
    public RuntimeException rethrowUnchecked() {
        return genericCastAndThrowException();
    }

    /**
     * Rethrows the wrapped exception as a checked exception. If there is no wrapped exception, the ILTISException rethrows itself as a checked
     * exception.
     *
     * @return Theoretically an {@code Exception} but that never happens, as an checked Exception is thrown in this method.
     */
    public Exception rethrowChecked() {
        return genericCastAndThrowException();
    }

    /**
     * Uses generic magic to "cast" a checked exception into an unchecked exception.
     *
     * @return Theoretically a kind of {@link RuntimeException}, but this never happens, as the method always throws.
     * @throws T
     * To make the magic work, this must be a {@code RuntimeException} or something which extends {@code RuntimeException}
     */
    @SuppressWarnings("unchecked")
    private <T extends Exception> T genericCastAndThrowException() throws T {
        if (originalException != null) {
            throw (T) originalException;
        } else {
            throw (T) this;
        }
    }

}
