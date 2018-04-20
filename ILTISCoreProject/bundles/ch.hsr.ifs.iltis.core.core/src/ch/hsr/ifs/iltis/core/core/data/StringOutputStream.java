package ch.hsr.ifs.iltis.core.core.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class StringOutputStream extends OutputStream {

   private ByteArrayOutputStream stream;

   @Override
   public void write(int b) throws IOException {
      stream.write(b);
   }

   public String toString() {
      return stream.toString();
   }
}
