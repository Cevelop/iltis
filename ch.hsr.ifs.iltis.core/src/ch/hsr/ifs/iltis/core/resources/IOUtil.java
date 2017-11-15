package ch.hsr.ifs.iltis.core.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;


public abstract class IOUtil {

   public static class FileIO {

      public static String read(final File file) throws IOException {
         return StringIO.write(new FileInputStream(file));
      }

      public static String read(final File file, final Charset sourceEncoding) throws IOException {
         return StringIO.write(new FileInputStream(file), sourceEncoding);
      }

      public static String read(final URL url) throws IOException {
         return StringIO.write(url.openStream());
      }

      public static String read(final URL url, final Charset sourceEncoding) throws IOException {
         return StringIO.write(url.openStream(), sourceEncoding);
      }

      public static void write(final File file, final String content) throws IOException {
         final BufferedWriter out = new BufferedWriter(new FileWriter(file));
         out.write(content);
         out.close();
      }

   }

   public static class StringIO {

      public static String write(final InputStream inputStream) throws IOException {
         return StringIO.write(inputStream, StandardCharsets.UTF_8);
      }

      public static String write(final InputStream inputStream, final Charset sourceEncoding) throws IOException {
         final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, sourceEncoding));
         String tmp = in.readLine();
         final StringBuilder sb = new StringBuilder(tmp != null ? tmp : ""); //$NON-NLS-1$
         while ((tmp = in.readLine()) != null) {
            sb.append(System.getProperty("line.separator")); //$NON-NLS-1$
            sb.append(tmp);
         }
         return sb.toString();
      }

      public static InputStream read(final String text) {
         return StringIO.read(text, StandardCharsets.UTF_8);
      }

      public static InputStream read(final String text, final Charset sourceEncoding) {
         return new ByteArrayInputStream(text.getBytes(sourceEncoding));
      }

   }

   public static Collection<File> allFilesRecursively(final File file) {
      final ArrayList<File> result = new ArrayList<>();
      if (file.isDirectory()) {
         for (final File child : file.listFiles()) {
            if (child != null) {
               result.addAll(allFilesRecursively(child));
            }
         }
      } else {
         result.add(file);
      }
      return result;
   }

   public static void safeClose(final Closeable toClose) {
      if (toClose != null) {
         try {
            toClose.close();
         }
         catch (final IOException ignored) {}
      }
   }


}
