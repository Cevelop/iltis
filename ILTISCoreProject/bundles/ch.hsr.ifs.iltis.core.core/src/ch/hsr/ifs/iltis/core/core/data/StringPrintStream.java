package ch.hsr.ifs.iltis.core.core.data;

import java.io.PrintStream;


public class StringPrintStream extends PrintStream {

   private StringOutputStream stream;

   private StringPrintStream(StringOutputStream stream) {
      super(stream);
      this.stream = stream;
   }

   public String toString() {
      return stream.toString();
   }

   public static StringPrintStream createNew() {
      return new StringPrintStream(new StringOutputStream());
   }
}
