package ch.hsr.ifs.iltis.core.core.data;

/**
 * A simple wrapper. This can be used to pass values out of a lambda.
 *
 * @author tstauber
 */
public class Wrapper<T> {

    public T wrapped;

    public Wrapper(final T target) {
        wrapped = target;
    }
}
