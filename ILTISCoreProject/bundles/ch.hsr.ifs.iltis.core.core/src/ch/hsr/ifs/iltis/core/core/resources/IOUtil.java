package ch.hsr.ifs.iltis.core.core.resources;

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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.iltis.core.core.resources.FileUtil;
import ch.hsr.ifs.iltis.core.core.resources.IOUtil;
import ch.hsr.ifs.iltis.core.core.resources.WorkspaceUtil;


/**
 * A utility class providing static methods to handle input/output. For file utility functionality please
 * refer to {@link FileUtil}
 * 
 * @author tstauber
 *
 */
public abstract class IOUtil {

   public static class FileIO {

      /**
       * Reads the whole content from a file
       * 
       * @param file
       *        The input file
       * @return The content of the file as a String
       * @throws IOException
       *         If an I/O error occurs
       */
      public static String read(final File file) throws IOException {
         return StringIO.write(new FileInputStream(file), StandardCharsets.UTF_8);
      }
      

      /**
       * Reads the whole content from a file
       * 
       * @param file
       *        The input file
       * @return The content of the file as a String
       * @throws IOException
       *         If an I/O error occurs
       * @throws CoreException 
       */
      public static String read(final IFile file) throws IOException, CoreException {
         return StringIO.write(file.getContents(), StandardCharsets.UTF_8);
      }

      /**
       * Reads the whole content from a file using the specified encoding
       * 
       * @param file
       *        The input file
       * @param sourceEncoding
       *        The encoding to use
       * @return The content of the file as a String
       * @throws IOException
       *         If an I/O error occurs
       */
      public static String read(final File file, final Charset sourceEncoding) throws IOException {
         return StringIO.write(new FileInputStream(file), sourceEncoding);
      }

      /**
       * Reads the whole content from a file
       * 
       * @param url
       *        The URL where to find the file
       * @return The content of the file as a String
       * @throws IOException
       *         If an I/O error occurs
       */
      public static String read(final URL url) throws IOException {
         return StringIO.write(url.openStream(), StandardCharsets.UTF_8);
      }

      /**
       * Reads the whole content from a file using the specified encoding
       * 
       * @param url
       *        The input file's url
       * @param sourceEncoding
       *        The encoding to use
       * @return The content of the file as a String
       * @throws IOException
       *         If an I/O error occurs
       */
      public static String read(final URL url, final Charset sourceEncoding) throws IOException {
         return StringIO.write(url.openStream(), sourceEncoding);
      }

      /**
       * Writes the content into the passed file
       * 
       * @param file
       *        The output file
       * @param content
       *        The content which will be written
       * @throws IOException
       *         If an I/O error occurs
       */
      public static void write(final File file, final String content) throws IOException {
         final BufferedWriter out = new BufferedWriter(new FileWriter(file));
         out.write(content);
         out.close();
      }

   }

   /**
    * A utility class providing static methods to handle input/output with Strings. For more comfort use {@linkplain IOUtil}
    * 
    * @author tstauber
    *
    */
   public static class StringIO {

      /**
       * Writes the content from the input stream to a String using the provided encoding
       * 
       * @param inputStream
       *        The input stream from which to read
       * @param sourceEncoding
       *        The encoding to use
       * @return A String containing the content of the input stream
       * @throws IOException
       *         If an I/O error occurs
       */
      public static String write(final InputStream inputStream, final Charset sourceEncoding) throws IOException {
         final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, sourceEncoding));
         String tmp = in.readLine();
         if (tmp == null) return "";
         final StringBuilder sb = new StringBuilder(tmp != null ? tmp : ""); //$NON-NLS-1$
         String lineSeparator = WorkspaceUtil.getWorkspaceLineSeparator();
         while ((tmp = in.readLine()) != null) {
            sb.append(lineSeparator); //$NON-NLS-1$
            sb.append(tmp);
         }
         return sb.toString();
      }

      /**
       * Reads the provided text into an input stream with utf-8 as default encoding
       * 
       * @param text
       *        The text to read into the stream
       * @return An input stream ready to provide the text
       */
      public static InputStream read(final String text) {
         return StringIO.read(text, StandardCharsets.UTF_8);
      }
     

      /**
       * Reads the provided text into an input stream using the provided encoding
       * 
       * @param text
       *        The text to read into the stream
       * @param sourceEncoding
       *        The encoding to use
       * @return An input stream ready to provide the text
       */
      public static InputStream read(final String text, final Charset sourceEncoding) {
         return new ByteArrayInputStream(text.getBytes(sourceEncoding));
      }

   }

   /**
    * Collects all files in the tree starting with root.
    * 
    * @param root
    *        The root node
    * @return A Collection containing either all child nodes, if root is a directory, or else only root
    */
   public static Collection<File> allFilesRecursively(final File root) {
      final ArrayList<File> result = new ArrayList<>();
      if (root.isDirectory()) {
         for (final File child : root.listFiles()) {
            if (child != null) {
               result.addAll(allFilesRecursively(child));
            }
         }
      } else {
         result.add(root);
      }
      return result;
   }

   /**
    * A helper method for save closing of Closeables
    * 
    * @param toClose
    */
   public static void safeClose(final Closeable toClose) {
      if (toClose != null) {
         try {
            toClose.close();
         } catch (final IOException ignored) {}
      }
   }

}
