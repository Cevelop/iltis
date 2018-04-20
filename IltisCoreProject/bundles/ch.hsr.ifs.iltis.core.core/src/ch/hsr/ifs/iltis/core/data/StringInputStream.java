package ch.hsr.ifs.iltis.core.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class StringInputStream extends InputStream {
   
   ByteArrayInputStream stream;
   
   public StringInputStream(String source) {
      this.stream = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
   }
   
   @Override
   public int read() throws IOException {
      return stream.read();
   }
   
}
