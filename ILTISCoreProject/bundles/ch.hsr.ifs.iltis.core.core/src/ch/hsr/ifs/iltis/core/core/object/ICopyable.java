package ch.hsr.ifs.iltis.core.core.object;

/**
 * An interface requesting an implementer to be copyable.
 * 
 * @author void
 * 
 * @since 1.1
 *
 */
public interface ICopyable<T extends ICopyable<T>> {

    T copy();

}
