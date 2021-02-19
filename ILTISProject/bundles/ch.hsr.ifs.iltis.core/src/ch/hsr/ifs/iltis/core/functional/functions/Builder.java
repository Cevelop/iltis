package ch.hsr.ifs.iltis.core.functional.functions;

/**
 * A functional interface which has as static method {@code build()} which creates a new object of type {@code T}. This is functionally equivalent to
 * a {@code Supplier<T>}, but describes better what it does.
 *
 * @author tstauber
 *
 * @param <T>
 * The type of the object to be created
 */
@FunctionalInterface
public interface Builder<T> {

    /**
     * Builds the content for this {@link Builder}
     *
     * @return An instance of {@link T}
     */
    T build();
}
