package ch.hsr.ifs.iltis.cpp.core.resources.info;

/**
 * An Exception call creating no stack trace. Used in the info system.
 * 
 * @since 1.1
 */
public class InfoException extends RuntimeException {

    private static final long serialVersionUID = -1967047532469794683L;

    public InfoException(final String message) {
        super(message, null, true, false);
    }

}
