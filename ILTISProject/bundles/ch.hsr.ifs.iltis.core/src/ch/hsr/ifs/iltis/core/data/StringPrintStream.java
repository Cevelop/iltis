package ch.hsr.ifs.iltis.core.data;

import java.io.PrintStream;


public class StringPrintStream extends PrintStream {

    private final StringOutputStream stream;

    private StringPrintStream(final StringOutputStream stream) {
        super(stream);
        this.stream = stream;
    }

    @Override
    public String toString() {
        return stream.toString();
    }

    public static StringPrintStream createNew() {
        return new StringPrintStream(new StringOutputStream());
    }
}
