package ch.hsr.ifs.iltis.core.object;

/**
 * An interface requesting an implementer to be copyable.
 * 
 * @author void
 * 
 * @since 2.0
 *
 */
public interface ICopyable<T extends ICopyable<T>> {

    T copy();

}
