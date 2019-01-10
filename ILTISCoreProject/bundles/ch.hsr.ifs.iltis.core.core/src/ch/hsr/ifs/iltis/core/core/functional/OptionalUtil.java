package ch.hsr.ifs.iltis.core.core.functional;

import static ch.hsr.ifs.iltis.core.core.functional.Functional.as;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ch.hsr.ifs.iltis.core.core.functional.functions.Function2;
import ch.hsr.ifs.iltis.core.core.functional.functions.Function3;
import ch.hsr.ifs.iltis.core.core.functional.functions.ThrowingConsumer;
import ch.hsr.ifs.iltis.core.core.functional.functions.ThrowingFunction;


/**
 * A utility class which provides static methods for operations on {@linkplain Optional} which exceed the possibilities implemented on Optional.
 *
 * @author tstauber
 *
 * @param <T>
 * The type wrapped by the Optional
 */
public class OptionalUtil<T> {

    private final Optional<T> optional;

    private OptionalUtil(final Optional<T> optional) {
        this.optional = optional;
    }

    public static <T> OptionalUtil<T> of(final Optional<T> optional) {
        return new OptionalUtil<>(optional);
    }

    public static <T> OptionalUtil<T> of(final T value) {
        return new OptionalUtil<>(Optional.ofNullable(value));
    }

    /**
     * This creates a new {@code OptionalUtil<T>} without having to cast the value.
     * If the creation fails this can be rather expensive, therefore this method should not be used to test if something is of a class;
     *
     * @param <T>
     * The type wrapped by the Optional
     * @param value
     * The original value to be wrapped in an {@code OptionalUtil<T>}
     * @throws ClassCastException
     * If the attempted cast failed
     * @return The wrapped value
     * @throws ClassCastException
     * If the {@code Object} is not castable to {@code T}
     */
    public static <T> OptionalUtil<T> asOf(final Object value) {
        try {
            return new OptionalUtil<>(Optional.ofNullable(as(value)));
        } catch (final ClassCastException e) {
            return EMPTY();
        }
    }

    public Optional<T> get() {
        return optional;
    }

    public <R> OptionalUtil<R> flatFlatMap(final Function<? super T, OptionalUtil<R>> mapper) {
        if (optional.isPresent()) return mapper.apply(optional.get());
        return EMPTY();
    }

    public <R> OptionalUtil<R> flatMap(final Function<? super T, Optional<R>> mapper) {
        return OptionalUtil.of(optional.flatMap(mapper));
    }

    public <R> OptionalUtil<R> map(final Function<? super T, R> mapper) {
        return OptionalUtil.of(optional.map(mapper));
    }

    public <X, R> OptionalUtil<R> map(final Function2<X, ? super T, R> mapper, final X x) {
        return OptionalUtil.of(optional.map(mapper.curry(x)));
    }

    public <X1, X2, R> OptionalUtil<R> map(final Function3<X1, X2, ? super T, R> mapper, final X1 x1, final X2 x2) {
        return OptionalUtil.of(optional.map(mapper.curry(x1).curry(x2)));
    }

    public <R> OptionalUtil<R> mapAs(final Class<R> clazz) {
        if (optional.isPresent() && clazz.isInstance(optional.get())) return OptionalUtil.of(optional.map(e -> clazz.cast(e)));
        return EMPTY();
    }

    public OptionalUtil<T> orElse(final Optional<T> opt) {
        if (optional.isPresent()) return this;
        return OptionalUtil.of(opt);
    }

    public OptionalUtil<T> orElse(final OptionalUtil<T> opt) {
        if (optional.isPresent()) return this;
        return opt;
    }

    public OptionalUtil<T> orElse(final T val) {
        if (optional.isPresent()) return this;
        return OptionalUtil.of(val);
    }

    /**
     * Executes the passed function, if the optional has a value present.
     *
     * @param funThen
     * A function which takes the extracted value of the passed Optional
     * @return this
     */
    public OptionalUtil<T> ifPresent(final Consumer<T> funThen) {
        optional.ifPresent(funThen::accept);
        return this;
    }

    public <E extends Throwable, X> OptionalUtil<X> ifPresent(final ThrowingFunction<T, X, E> funThen,
            final Function<Throwable, X> exceptionHandler) {
        if (optional.isPresent()) {
            try {
                return OptionalUtil.of(funThen.apply(optional.get()));
            } catch (final Throwable e) {
                return OptionalUtil.of(exceptionHandler.apply(e));
            }
        }
        return EMPTY();
    }

    /**
     * Executes the passed function, if the optional has no value present.
     *
     * @param funThen
     * A function which takes the extracted value of the passed Optional
     * @return this
     */
    public OptionalUtil<T> ifNotPresent(final Supplier<T> funThen) {
        if (!optional.isPresent()) funThen.get();
        return this;
    }

    public OptionalUtil<T> ifNotPresent(final Runnable funThen) {
        if (!optional.isPresent()) funThen.run();
        return this;
    }

    /**
     * Executes the passed function, if the optional has a value present.
     *
     * @param <E>
     * The exception type
     * @param funThen
     * A function which can throw and takes the extracted value of the passed Optional
     * @throws E
     * pass-through from funThen
     * @return this
     */
    public <E extends Exception> OptionalUtil<T> ifPresentT(final ThrowingConsumer<T, E> funThen) throws E {
        if (optional.isPresent()) funThen.accept(optional.get());
        return this;
    }

    /**
     * Executes the passed function, if the optional has no value present.
     *
     * @param <E>
     * The exception type
     * @param funThen
     * A function which can throw and takes the extracted value of the passed Optional
     * @throws E
     * pass-through from funThen
     * @return this
     */
    public <E extends Exception> OptionalUtil<T> ifNotPresentT(final ThrowingConsumer<T, E> funThen) throws E {
        if (!optional.isPresent()) funThen.accept(optional.get());
        return this;
    }

    /**
     * Executes the passed function, if the optional has a value present.
     *
     * @param funThen
     * A function which takes the extracted value of the passed Optional
     * @return this
     */
    public OptionalUtil<T> useIfPresent(final Consumer<Optional<T>> funThen) {
        if (optional.isPresent()) funThen.accept(optional);
        return this;
    }

    public void useIn(final Consumer<Optional<T>> fun) {
        fun.accept(optional);
    }

    public <E extends Throwable> void useInT(final ThrowingConsumer<Optional<T>, E> fun) throws E {
        fun.accept(optional);
    }

    protected static <S> OptionalUtil<S> EMPTY() {
        return OptionalUtil.of(Optional.empty());
    }
}
