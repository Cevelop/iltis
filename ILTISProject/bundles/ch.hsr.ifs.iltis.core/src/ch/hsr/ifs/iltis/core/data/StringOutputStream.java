package ch.hsr.ifs.iltis.core.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class StringOutputStream extends OutputStream {

    private ByteArrayOutputStream stream;

    @Override
    public void write(final int b) throws IOException {
        stream.write(b);
    }

    @Override
    public String toString() {
        return stream.toString();
    }
}
