package ch.hsr.ifs.iltis.core.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class StringInputStream extends InputStream {

    ByteArrayInputStream stream;

    public StringInputStream(final String source) {
        stream = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

}
