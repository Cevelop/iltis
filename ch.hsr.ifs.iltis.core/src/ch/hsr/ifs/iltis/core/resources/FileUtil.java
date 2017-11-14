package ch.hsr.ifs.iltis.core.resources;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import ch.hsr.ifs.iltis.core.exception.ILTISException;


@SuppressWarnings("unused")
public abstract class FileUtil {


   /* IFile */

   //DOC missing
   public static IFile toIFile(final File file) {
      return toIFile(file.toURI());
   }

   //DOC missing
   public static IFile toIFile(final String filePath) {
      return toIFile(new File(filePath));
   }

   //DOC missing
   public static IFile toIFile(final IPath filePath) {
      return ProjectUtil.getWorkspaceRoot().getFile(filePath);
   }

   //DOC missing
   private static IFile toIFile(final URI fileURI) {
      final IFile[] files = ProjectUtil.getWorkspaceRoot().findFilesForLocationURI(fileURI);

      if (files.length == 1) {
         return files[0];
      }

      for (final IFile file : files) {
         if (fileURI.getPath().endsWith(file.getFullPath().toString())) {
            return file;
         }
      }

      return null;
   }


   /* File */
   //DOC missing
   public static File toFile(final IFile file) {
      return toFile(file.getLocation());
   }

   //DOC missing
   public static File toFile(final String filePath) {
      return new File(filePath);
   }

   //DOC missing
   public static File toFile(final IPath filePath) {
      return filePath.toFile();
   }

   //DOC missing
   private static File toFile(final URI fileURI) {
      return toFile(toIFile(fileURI));
   }


   /* Other */
   //DOC missing
   public static String getFilenameWithoutExtension(final String filename) {
      final int begin = filename.lastIndexOf(File.separator) + 1;
      int end = filename.lastIndexOf('.');
      if (end < 0) {
         end = filename.length();
      }
      return filename.substring(begin, end);
   }

   //DOC missing
   public static String getFilePart(final String filePath) {
      final Path path = new Path(filePath);
      ILTISException.Unless.isTrue(path.segmentCount() > 0, "Path elements must not be empty");
      return path.segment(path.segmentCount() - 1);
   }

   //DOC missing
   public static Path getPath(final IFile file) {
      final String pathOfFile = file.getFullPath().toOSString();
      return new Path(removeFilePart(pathOfFile));
   }

   //DOC missing
   public static String removeFilePart(final String filePath) {
      return filePath.replaceAll("(\\w)*\\.(\\w)*", "");
   }

   //DOC missing
   public static URI stringToUri(final String fileString) {
      return new File(fileString).toURI();
   }
}
