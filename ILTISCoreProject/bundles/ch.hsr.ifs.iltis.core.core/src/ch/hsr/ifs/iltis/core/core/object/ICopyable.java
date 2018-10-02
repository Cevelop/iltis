package ch.hsr.ifs.iltis.core.core.object;

/**
 * TODO
 *
 */
public interface ICopyable<T extends ICopyable<T>> {

    T copy();
}
